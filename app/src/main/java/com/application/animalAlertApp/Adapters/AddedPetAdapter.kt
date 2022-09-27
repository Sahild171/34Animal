package com.application.animalAlertApp.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.application.animalAlertApp.Interfaces.SeePetInterface
import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.R
import com.application.animalAlertApp.data.Response.MessageXX
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class AddedPetAdapter(
    val context: Context?,
    val pet_list: ArrayList<MessageXX>,
    val size: Int,
    val seePetInterface: SeePetInterface
) : RecyclerView.Adapter<AddedPetAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.added_pet_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //  holder.img_pet.setImageResource(R.mipmap.dog)
    Log.e("profileImage",""+pet_list[position].profileImage)
            Glide.with(context!!)
                .load(ApiService.PET_IMAGE_BASE_URL + pet_list[position].profileImage)
                .diskCacheStrategy(
                    DiskCacheStrategy.ALL
                ).into(holder.img_pet)

        holder.img_pet.setOnClickListener {
            seePetInterface.onseepet(position, pet_list[position])
        }
    }

    override fun getItemCount(): Int {
        return size
    }

    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val img_pet: ImageView = itemview.findViewById(R.id.img_pet)
    }
}