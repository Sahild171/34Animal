package com.application.animalAlertApp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.animalAlertApp.R
import com.application.animalAlertApp.data.Response.Payment.FindHistory


class TransactionAdapter(val context: Context, val list: List<FindHistory>) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {


    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val tv_price: TextView = itemview.findViewById(R.id.tv_price)
        val tv_message: TextView = itemview.findViewById(R.id.tv_message)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_price.setText(list[position].amount)
        holder.tv_message.setText("Payment Received")

    }

    override fun getItemCount(): Int {
        return list.size
    }
}