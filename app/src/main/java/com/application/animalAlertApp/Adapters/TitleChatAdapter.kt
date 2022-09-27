package com.application.animalAlertApp.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.animalAlertApp.Model.UsersChatList
import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.R
import com.application.animalAlertApp.Utils.convertLongToTime
import com.application.animalAlertApp.View.Chats.ChatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlin.collections.ArrayList

class TitleChatAdapter(
    var context: Context?,
    var list: ArrayList<UsersChatList>,
    val userid: String
) : RecyclerView.Adapter<TitleChatAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.title_chat_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_name.setText(list.get(position).name)
        holder.tv_message.setText(list.get(position).msg)
        if (list.get(position).type.equals("Message")) {
            holder.tv_message.setText(list.get(position).msg)
        } else {
            holder.tv_message.setText("Image")
        }

        holder.tv_time.setText(convertLongToTime(list[position].timestamp.toLong()))
        Glide.with(context!!).load(ApiService.IMAGE_BASE_URL + list[position].image)
            .diskCacheStrategy(
                DiskCacheStrategy.ALL
            ).placeholder(R.drawable.placeholder).into(holder.img_profile)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("ReceverId", list.get(position).id)
            intent.putExtra("ReceverName", list.get(position).name)
            intent.putExtra("ReceverImage", list.get(position).image)
            intent.putExtra("ChatRoom", getchatroom(userid, list.get(position).id))
            intent.putExtra("devicetoken", list[position].devicetoken)
            context?.startActivity(intent)

        }
    }

    fun setData(list: ArrayList<UsersChatList>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val tv_name: TextView = itemView.findViewById(R.id.tv_name)
        val tv_message: TextView = itemView.findViewById(R.id.tv_message)
        val tv_time: TextView = itemView.findViewById(R.id.textView68)
        val img_profile: ImageView = itemView.findViewById(R.id.img_profile)

    }


    private fun getchatroom(myid: String, otherid: String): String {
        val chatRoom: String
        chatRoom = if (myid.compareTo(otherid) < 0) {
            // ToMessageId has less alphabetic order then fromUserId
            myid + "_" + otherid
        } else {
            otherid + "_" + myid
            //     chatRoom = toMessageId + "_" + fromUserId;
        }
        return chatRoom
    }

}