package com.application.animalAlertApp.Adapters

import androidx.viewpager.widget.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.widget.ImageView
import android.view.LayoutInflater
import android.widget.TextView
import com.application.animalAlertApp.Model.ImageModel
import com.application.animalAlertApp.R
import java.util.ArrayList



class WelcomePageAdapter(var context: Context, var pager: ArrayList<ImageModel>) : PagerAdapter() {


    override fun getCount(): Int {
        return pager!!.size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view === o
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view: View = LayoutInflater.from(context).inflate(R.layout.welcome_item, container, false)
        val imageView = view.findViewById<View>(R.id.img_logo) as ImageView
        val tv_description=view.findViewById<View>(R.id.tv_description) as TextView
        imageView.setBackgroundResource(pager[position].image)
        tv_description.setText(pager[position].description)
        container.addView(view)
        return view
    }




    override fun getItemPosition(`object`: Any): Int {
        return super.getItemPosition(`object`)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)

        }
    }


