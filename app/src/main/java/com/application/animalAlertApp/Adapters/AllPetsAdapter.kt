package com.application.animalAlertApp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.animalAlertApp.Interfaces.SeePetInterface
import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.R
import com.application.animalAlertApp.data.Response.MessageXX
import com.bumptech.glide.Glide

class AllPetsAdapter(
    val context: Context,
    val list: List<MessageXX>,
    val seePetInterface: SeePetInterface) : RecyclerView.Adapter<AllPetsAdapter.ViewHolder>() {


    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val img_profile: ImageView = itemView.findViewById(R.id.img_profile)
        val tv_name: TextView = itemView.findViewById(R.id.tv_name)
        val tv_color: TextView = itemView.findViewById(R.id.tv_color)
        val tv_breed: TextView = itemView.findViewById(R.id.tv_breed)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.all_pets_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list[position].petImages.size > 0) {

            Glide.with(context).load(ApiService.PET_IMAGE_BASE_URL + list[position].petImages[0])
                .placeholder(R.drawable.image_placeholder)
                .into(holder.img_profile)
        }

        holder.tv_name.setText(list[position].petName)
        holder.tv_color.setText(list[position].petColor)
        holder.tv_breed.setText(list[position].petBreed)
        holder.itemView.setOnClickListener {
            seePetInterface.onseepet(position, list[position])
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}