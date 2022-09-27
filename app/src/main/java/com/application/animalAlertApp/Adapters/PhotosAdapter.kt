package com.application.animalAlertApp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.application.animalAlertApp.Interfaces.DeletePortfolio
import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class PhotosAdapter(
    var context: Context?,
    val list: ArrayList<String>,
    val delete: DeletePortfolio,
    val bussUserId: String,
    val myuserId: String
) :
    RecyclerView.Adapter<PhotosAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.photos_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context!!).load(ApiService.SHOP_IMAGE_BASE_URL + list[position])
            .diskCacheStrategy(
                DiskCacheStrategy.ALL
            ).placeholder(R.drawable.image_placeholder).into(holder.img_pet)

        holder.itemView.setOnLongClickListener {
            if (bussUserId.equals(myuserId)) {
                holder.constraint_delete.visibility = View.VISIBLE
            }
            true
        }

        holder.constraint_delete.setOnClickListener {
            holder.constraint_delete.visibility = View.GONE
        }
        holder.img_delete.setOnClickListener {
            delete.ondelete(position, list[position])
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val img_delete: ImageView = itemview.findViewById(R.id.img_delete)
        val img_pet: ImageView = itemview.findViewById(R.id.img_pet)
        val constraint_delete: ConstraintLayout = itemview.findViewById(R.id.constraint_delete)

    }
}