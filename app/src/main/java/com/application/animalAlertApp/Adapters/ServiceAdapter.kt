package com.application.animalAlertApp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.application.animalAlertApp.Interfaces.ChangeServicesInterface
import com.application.animalAlertApp.R
import com.application.animalAlertApp.data.Response.BusinessProfile.Service

class ServiceAdapter(
    val context: Context?,
    val list: ArrayList<Service>,
    val changeservice: ChangeServicesInterface, val bussUserID: String, val myuserId: String
) : RecyclerView.Adapter<ServiceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.service_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_title.setText(list[position].service)
        holder.tv_description.setText(list[position].serviceDescription)
        holder.tv_price.setText("$" + list[position].price + " " + list[position].pricePeriod)

        holder.itemView.setOnLongClickListener {
            if (bussUserID.equals(myuserId)) {
                holder.constarint_modification.visibility = View.VISIBLE
            }
            true
        }
        holder.constarint_modification.setOnClickListener {
            holder.constarint_modification.visibility = View.GONE
        }
        holder.img_delete.setOnClickListener {
            holder.constarint_modification.visibility = View.GONE
            changeservice.ondelete(position, list[position]._id)
        }
        holder.img_edit.setOnClickListener {
            holder.constarint_modification.visibility = View.GONE
            changeservice.onedit(position, list[position])
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val tv_title: TextView = itemview.findViewById(R.id.tv_title)
        val tv_description: TextView = itemview.findViewById(R.id.tv_description)
        val tv_price: TextView = itemview.findViewById(R.id.tv_price)
        val img_edit: ImageView = itemview.findViewById(R.id.img_edit)
        val img_delete: ImageView = itemview.findViewById(R.id.img_delete)
        val constarint_modification: ConstraintLayout =
            itemview.findViewById(R.id.constarint_modification)
    }
}


