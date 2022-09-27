package com.application.animalAlertApp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.animalAlertApp.Interfaces.ShopServiceInterface
import com.application.animalAlertApp.data.Response.BusinessProfile.Service
import com.application.animalAlertApp.databinding.EditserviceItemBinding

class EditServiceAdapter(val context: Context,val service_list: ArrayList<Service>,val  shop:ShopServiceInterface):RecyclerView.Adapter<EditServiceAdapter.ViewHolder>() {
    inner class ViewHolder(var binding: EditserviceItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            EditserviceItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.imgCross.visibility=View.INVISIBLE
        holder.binding.tvDescription.setText(service_list[position].serviceDescription)
        holder.binding.tvPrice.setText("Price: $"+service_list[position].price)
        holder.binding.tvService.setText(service_list[position].service)

        holder.itemView.setOnClickListener {
            shop.ondelete(position)
        }
    }

    override fun getItemCount(): Int {
        return service_list.size
    }
}