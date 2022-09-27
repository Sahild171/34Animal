package com.application.animalAlertApp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.animalAlertApp.R

class TitleAdapter(var context: Context?) : RecyclerView.Adapter<TitleAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.title_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.tv_name.setText("Looking For Dog")

    }

    override fun getItemCount(): Int {
        return 4
    }

    class ViewHolder(item: View):RecyclerView.ViewHolder(item){
        val tv_name: TextView = itemView.findViewById(R.id.tv_name)
    }

}