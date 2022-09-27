package com.application.animalAlertApp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.animalAlertApp.Interfaces.RequestInterface
import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.R
import com.application.animalAlertApp.data.Response.Request.GetRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.button.MaterialButton
import de.hdodenhof.circleimageview.CircleImageView

class RequestAdapter(
    val context: Context,
    val requestInterface: RequestInterface,
    var list: List<GetRequest>
) : RecyclerView.Adapter<RequestAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.request_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(ApiService.IMAGE_BASE_URL + list[position].userImage).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.placeholder).into(holder.img_profile)
        holder.tv_name.setText(list[position].userName)

//        holder.tv_date.setText(changeformat(list[position].status))

        holder.bt_accept.setOnClickListener {
            requestInterface.onAccept(position, list[position]._id,list[position].senderId)
        }
        holder.bt_decline.setOnClickListener {
            requestInterface.onDecline(position, list[position]._id)
        }
    }

    fun setData(list: List<GetRequest>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val img_profile = itemview.findViewById<CircleImageView>(R.id.circleImageView)
        val tv_name = itemview.findViewById<TextView>(R.id.tv_name)
        val tv_date = itemview.findViewById<TextView>(R.id.textView24)
        val bt_accept = itemview.findViewById<MaterialButton>(R.id.bt_accept)
        val bt_decline = itemview.findViewById<MaterialButton>(R.id.bt_decline)
    }


}