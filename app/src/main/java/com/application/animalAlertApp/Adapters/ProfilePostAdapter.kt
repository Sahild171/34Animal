package com.application.animalAlertApp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.application.animalAlertApp.Model.ImageModel
import com.application.animalAlertApp.R
import com.application.animalAlertApp.data.Response.Post.GetPost
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.collections.ArrayList

class ProfilePostAdapter(val context: Context?,val post_list: ArrayList<GetPost>, val see_post: Int) :
    RecyclerView.Adapter<ProfilePostAdapter.ViewHolder>() {
    var arrayList: ArrayList<ImageModel>? = null
    var adapters:ProfilePostPagerAdapter?=null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.profile_post_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_description.setText(post_list[position].description)
        setViewPager(holder,post_list[position].postImg)

    }

    override fun getItemCount(): Int {
        return see_post
    }

    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        var viewpager: ViewPager2 = itemview.findViewById(R.id.viewpager)
        var tablayout: TabLayout = itemview.findViewById(R.id.tablayout)
        val tv_description:TextView = itemview.findViewById(R.id.tv_description)
    }

    private fun setViewPager(holder: ViewHolder, postImg: List<String>) {
        adapters= ProfilePostPagerAdapter(context!!)
        adapters?.setItem(postImg)
        holder.viewpager.adapter=adapters
        TabLayoutMediator(holder.tablayout, holder.viewpager) { tab, position ->

        }.attach()
    }
}