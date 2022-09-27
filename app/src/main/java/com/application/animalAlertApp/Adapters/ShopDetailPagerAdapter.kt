package com.application.animalAlertApp.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class ShopDetailPagerAdapter(val context: Context) :
    RecyclerView.Adapter<ShopDetailPagerAdapter.ShopViewHolder>() {
    private var list: List<String> = listOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        return ShopViewHolder(parent)
    }


    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        //   holder.imageView.setImageResource(list.get(position).image)
     //   Log.e("images", "" + list.get(position))


        Glide.with(context)
            .load(ApiService.PET_IMAGE_BASE_URL + list.get(position)).fitCenter().centerCrop()
            .placeholder(R.drawable.image_placeholder).diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.imageView)
     //   setimageheight(holder.imageView)
//        holder.tv_description.setText(list.get(position).description)
    }

    fun setItem(list: List<String>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size

    class ShopViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        constructor(parent: ViewGroup) : this(
            LayoutInflater.from(parent.context).inflate(
                R.layout.shopdetail_item,
                parent, false
            )
        )
        val imageView = itemView.findViewById<View>(R.id.img_logo) as ImageView

    }

    fun setimageheight(iv:ImageView) {
        //  val newHeight = 800 // New height in pixels
        val newWidth =  700 // New width in pixels
        iv.requestLayout()
        iv.getLayoutParams().width =newWidth
        // iv.getLayoutParams().height = newHeight
        iv.setScaleType(ImageView.ScaleType.FIT_XY)
    }

}