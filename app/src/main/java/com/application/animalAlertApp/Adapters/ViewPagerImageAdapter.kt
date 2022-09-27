package com.application.animalAlertApp.Adapters


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class ViewPagerImageAdapter(
    var context: Context?,
    var images: List<String>,
    var viewPager: ViewPager
) : PagerAdapter() {

    private var layoutInflater: LayoutInflater? = null


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imgDisplay: ImageView
        val imgLeft: ImageView
        val imgRight: ImageView
        layoutInflater = context?.getSystemService(
            Context.LAYOUT_INFLATER_SERVICE
        ) as LayoutInflater?
        val view: View = layoutInflater?.inflate(R.layout.single_image_preview, container, false)!!

        imgDisplay = view.findViewById<View>(R.id.imageView6) as ImageView
        imgLeft = view.findViewById<View>(R.id.arrow_left) as ImageView
        imgRight = view.findViewById<View>(R.id.arrow_right) as ImageView

        imgLeft.setOnClickListener { viewPager.setCurrentItem(getItem(-1), true) }
        imgRight.setOnClickListener { viewPager.setCurrentItem(getItem(+1), true) }
//        val image: ImageModel = images[position]
//        imgDisplay.setImageResource(image.image)
        Glide.with(context!!).load(ApiService.POST_IMAGE_BASE_URL+images[position]).fitCenter().centerCrop()
            .placeholder(R.drawable.image_placeholder).diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imgDisplay)

        if (images.size==1){
            imgLeft.visibility=View.INVISIBLE
            imgRight.visibility=View.INVISIBLE
        }else{
            imgLeft.visibility=View.VISIBLE
            imgRight.visibility=View.VISIBLE
        }
        viewPager.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                Log.e("tag","pagescrolled")
            }

            override fun onPageSelected(position: Int) {
               if (position.equals(images.size-1)){
                   imgRight.visibility=View.INVISIBLE
               }else{
                   imgRight.visibility=View.VISIBLE
               }
            }

            override fun onPageScrollStateChanged(state: Int) {
                Log.e("tag","pagescrolledstate")
            }

        })
        container.addView(view);
        return view
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj as View
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View?)
    }

    private fun getItem(i: Int): Int {
        return viewPager.getCurrentItem() + i
    }

    private fun setCurrentItem(position: Int) {
        viewPager.setCurrentItem(position, false)
    }




}