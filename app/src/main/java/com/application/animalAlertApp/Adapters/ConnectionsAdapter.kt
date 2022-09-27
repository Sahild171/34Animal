package com.application.animalAlertApp.Adapters

import android.content.Context
import android.content.Intent
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.application.animalAlertApp.Interfaces.RemoveFriend
import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.R
import com.application.animalAlertApp.SharedPrefrences.MySharedPreferences
import com.application.animalAlertApp.View.Requsts.ProfileActivity
import com.application.animalAlertApp.data.Response.Request.GetRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import de.hdodenhof.circleimageview.CircleImageView


class ConnectionsAdapter(
    val context: Context,
    var list: List<GetRequest>,
    val remove: RemoveFriend
) :
    RecyclerView.Adapter<ConnectionsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.connection_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_name.setText(list[position].userName)
        holder.tv_location.setText(list[position].userLocation)
        Glide.with(context).load(ApiService.IMAGE_BASE_URL + list[position].userImage)
            .diskCacheStrategy(
                DiskCacheStrategy.ALL
            ).placeholder(R.drawable.placeholder).into(holder.img_profile)


        holder.img_dots.setOnClickListener {
            ShowPopMenu(holder, position, list[position]._id)
        }


        holder.itemView.setOnClickListener {
            val sharedPreferences = MySharedPreferences(context)
            if (list[position].senderId.equals(sharedPreferences.getUserid())) {
                val intent = Intent(context, ProfileActivity::class.java)
                intent.putExtra("userid", list[position].recieverId)
                context.startActivity(intent)
            } else {
                val intent = Intent(context, ProfileActivity::class.java)
                intent.putExtra("userid", list[position].senderId)
                context.startActivity(intent)

            }
        }
    }

    fun setData(list: List<GetRequest>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun ShowPopMenu(holder: ViewHolder, pos: Int, id: String) {
        val popup = PopupMenu(context, holder.img_dots, Gravity.END, 0, R.style.MyPopupMenu)
        popup.inflate(R.menu.remove_popmenu)
        popup.show()
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.remove -> {
                    remove.onRemove(pos, id)
                }
            }
            true
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val img_profile: CircleImageView = itemview.findViewById(R.id.img_profile)
        val tv_name: TextView = itemview.findViewById(R.id.tv_name)
        val tv_location: TextView = itemview.findViewById(R.id.tv_location)
        val img_dots: ImageView = itemview.findViewById(R.id.img_dots)
    }
}