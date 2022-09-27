package com.application.animalAlertApp.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.R
import com.application.animalAlertApp.Utils.changeformat
import com.application.animalAlertApp.View.BusinessProfile.BusinessProfileActivity
import com.application.animalAlertApp.data.Response.Shop.GetShop
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import de.hdodenhof.circleimageview.CircleImageView


class ShopAdapter(private val context: Context?, var list: List<GetShop>) :
    RecyclerView.Adapter<ShopAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.shop_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_name.setText(list[position].businessName)

            Glide.with(context!!).load(ApiService.SHOP_IMAGE_BASE_URL + list[position].uploadLogo).fitCenter().centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.placeholder).into(holder.img_circle)

        if (list[position].portfolioLogo.size > 0) {
            Glide.with(context)
                .load(ApiService.SHOP_IMAGE_BASE_URL + list[position].portfolioLogo[0])
                .diskCacheStrategy(
                    DiskCacheStrategy.ALL
                ).placeholder(R.drawable.image_placeholder).into(holder.img_portfolio)
        } else {
            Glide.with(context)
                .load(ApiService.SHOP_IMAGE_BASE_URL)
                .diskCacheStrategy(
                    DiskCacheStrategy.ALL
                ).placeholder(R.drawable.image_placeholder).into(holder.img_portfolio)
        }
        holder.tv_date.setText(changeformat(list.get(position).createdAt))
        holder.tv_bussdescription.setText(list[position].businessDescription)


        holder.itemView.setOnClickListener {
            val intent= Intent(context, BusinessProfileActivity::class.java)
            intent.putExtra("userid",list[position].userId)
            context.startActivity(intent)
        }

    }

    fun setdata(list: ArrayList<GetShop>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tv_name: TextView = itemView.findViewById(R.id.tv_name)
        val tv_bussdescription: TextView = itemView.findViewById(R.id.tv_bussdescription)
        val tv_date: TextView = itemView.findViewById(R.id.tv_date)
        val img_circle: CircleImageView = itemView.findViewById(R.id.circleImageView2)
        val img_portfolio: ImageView = itemView.findViewById(R.id.img_portfolio)
    }

}