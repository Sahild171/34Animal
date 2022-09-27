package com.application.animalAlertApp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.animalAlertApp.R
import com.application.animalAlertApp.Utils.changeformat
import com.application.animalAlertApp.data.Response.GetNotification
import com.application.animalAlertApp.databinding.NotificationItemBinding


class NotificationsAdapter(val context: Context?, var list: List<GetNotification>) :
    RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: NotificationItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            NotificationItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvTitle.setText(list[position].notification)
        holder.binding.tvDate.setText(changeformat(list[position].createdAt))


        if (!list[position].notificationType.isNullOrEmpty()) {
            if (list[position].notificationType.equals("Comment")) {
                holder.binding.view1.setBackgroundColor(context?.resources!!.getColor(R.color.yellow))
            } else if (list[position].notificationType.equals("Request")) {
                holder.binding.view1.setBackgroundColor(context?.resources!!.getColor(R.color.green))
            } else if (list[position].notificationType.equals("Like")) {
                holder.binding.view1.setBackgroundColor(context?.resources!!.getColor(R.color.quantum_orange))
            } else {
                holder.binding.view1.setBackgroundColor(context?.resources!!.getColor(R.color.green))
            }
        }
    }


    fun setData(list: List<GetNotification>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }


}