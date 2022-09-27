package com.application.animalAlertApp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.animalAlertApp.Interfaces.ShopServiceInterface
import com.application.animalAlertApp.R
import org.json.JSONArray

class SelectServiceAdapter(
    context: Context?,
    val jsonArray: JSONArray,
    val delete: ShopServiceInterface) : RecyclerView.Adapter<SelectServiceAdapter.ViewHolder>() {


    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val tv_service = itemview.findViewById<TextView>(R.id.tv_service)
        val tv_price = itemview.findViewById<TextView>(R.id.tv_price)
        val tv_description = itemview.findViewById<TextView>(R.id.tv_description)
        val img_cross = itemview.findViewById<ImageView>(R.id.img_cross)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.add_service_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_service.setText(jsonArray.getJSONObject(position).getString("Service"))
        val priceperiod = jsonArray.getJSONObject(position).getString("pricePeriod")
        holder.tv_price.setText("Price: $" + jsonArray.getJSONObject(position).getString("price") + "" + priceperiod)
        holder.tv_description.setText(jsonArray.getJSONObject(position).getString("serviceDescription"))

        holder.img_cross.setOnClickListener {
            delete.ondelete(position)
        }

    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }
}