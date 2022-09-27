package com.application.animalAlertApp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class ProfilePostPagerAdapter(val context: Context) : RecyclerView.Adapter<ProfilePostPagerAdapter.ProfilePostViewHolder>() {


    private var list: List<String>?=null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfilePostViewHolder {
        return ProfilePostViewHolder(parent)
    }


    override fun onBindViewHolder(holder: ProfilePostViewHolder, position: Int) {
      //  holder.imageView.setImageResource(list[position])

        Glide.with(context).load(ApiService.POST_IMAGE_BASE_URL+ list!![position]).fitCenter().centerCrop().placeholder(R.drawable.image_placeholder)
            .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imageView)
    }

    fun setItem(list: List<String>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list?.size!!

    class ProfilePostViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        constructor(parent: ViewGroup) : this(
            LayoutInflater.from(parent.context).inflate(
                R.layout.profile_post_item,
                parent, false


            )
        )
        val imageView = itemView.findViewById<View>(R.id.img_logo) as ImageView


    }
}