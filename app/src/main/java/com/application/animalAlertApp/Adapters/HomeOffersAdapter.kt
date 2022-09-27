package com.application.animalAlertApp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.animalAlertApp.Model.ImageModel
import com.application.animalAlertApp.R

class HomeOffersAdapter : RecyclerView.Adapter<HomeOffersAdapter.HomeOffersViewHolder>() {


    private var list: List<ImageModel> = listOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeOffersViewHolder {
        return HomeOffersViewHolder(parent)
    }


    override fun onBindViewHolder(holder: HomeOffersViewHolder, position: Int) {
      holder.imageView.setImageResource(list.get(position).image)
        holder.tv_description.setText(list.get(position).description)
    }

    fun setItem(list: List<ImageModel>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size

    class HomeOffersViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        constructor(parent: ViewGroup) : this(
            LayoutInflater.from(parent.context).inflate(
                R.layout.welcome_item,
                parent, false


            )
        )
        val imageView = itemView.findViewById<View>(R.id.img_logo) as ImageView
        val tv_description=itemView.findViewById<View>(R.id.tv_description) as TextView

    }

}