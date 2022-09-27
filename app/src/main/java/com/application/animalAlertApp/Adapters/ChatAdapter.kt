package com.application.animalAlertApp.Adapters

import android.content.Context
import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.animalAlertApp.Interfaces.Iteminterface
import com.application.animalAlertApp.Model.Chat
import com.application.animalAlertApp.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.application.animalAlertApp.View.Chats.ZoomImageActivity

class ChatAdapter(
    val context: Context?,
    val list: List<Chat>,
    val userid: String, val clicks: Iteminterface
) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {


    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val messageRecived = itemView.findViewById<View>(R.id.messageRecived) as TextView
        val messageSend = itemView.findViewById<View>(R.id.messageSend) as TextView
        val msg_rec_time = itemView.findViewById<TextView>(R.id.msg_rec_time) as TextView
        val msg_send_time = itemView.findViewById<TextView>(R.id.msg_send_time) as TextView
        val img_send = itemview.findViewById<ImageView>(R.id.img_send) as ImageView
        val img_recieve = itemview.findViewById<ImageView>(R.id.img_recieve) as ImageView
        val linear = itemview.findViewById<LinearLayout>(R.id.linear) as LinearLayout
        val pet_image = itemview.findViewById<ImageView>(R.id.pet_image) as ImageView
        val tv_questions = itemView.findViewById<View>(R.id.tv_questions) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chatting_items, parent, false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(itemholder: ViewHolder, position: Int) {

//            val itemholder = holder
        if (list[itemholder.layoutPosition].type.equals("Message")) {
            if (userid.equals(list.get(itemholder.layoutPosition).senderid)) {
                itemholder.messageSend.visibility = View.VISIBLE
                itemholder.messageRecived.visibility = View.GONE
                itemholder.messageSend.setText(list.get(itemholder.layoutPosition).msg)
                itemholder.img_send.visibility = View.GONE
                itemholder.img_recieve.visibility = View.GONE
                itemholder.linear.visibility = View.GONE
            } else {
                itemholder.messageSend.visibility = View.GONE
                itemholder.messageRecived.visibility = View.VISIBLE
                itemholder.messageRecived.setText(list.get(itemholder.layoutPosition).msg)
                itemholder.img_send.visibility = View.GONE
                itemholder.img_recieve.visibility = View.GONE
                itemholder.linear.visibility = View.GONE
            }
        } else if (list[itemholder.layoutPosition].type.equals("Questions")) {
            itemholder.linear.visibility = View.VISIBLE
            if (list[position].petimage.isEmpty()) {
                itemholder.pet_image.visibility = View.GONE
            } else {
                itemholder.pet_image.visibility = View.VISIBLE
                Glide.with(context!!).load(list.get(position).petimage).into(itemholder.pet_image)
            }
            itemholder.tv_questions.setText(Html.fromHtml(list[position].questionstext))
            itemholder.img_send.visibility = View.GONE
            itemholder.img_recieve.visibility = View.GONE
            itemholder.messageSend.visibility = View.GONE
            itemholder.messageRecived.visibility = View.GONE


        } else {
            if (userid.equals(list.get(itemholder.layoutPosition).senderid)) {
                itemholder.img_send.visibility = View.VISIBLE
                itemholder.img_recieve.visibility = View.GONE
                itemholder.messageSend.visibility = View.GONE
                itemholder.messageRecived.visibility = View.GONE
                itemholder.linear.visibility = View.GONE
                Glide.with(context!!).load(list[itemholder.layoutPosition].image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(itemholder.img_send)
            } else {
                itemholder.img_recieve.visibility = View.VISIBLE
                itemholder.img_send.visibility = View.GONE
                itemholder.messageSend.visibility = View.GONE
                itemholder.messageRecived.visibility = View.GONE
                itemholder.linear.visibility = View.GONE
                Glide.with(context!!).load(list[itemholder.layoutPosition].image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(itemholder.img_recieve)
            }
        }

        itemholder.img_send.setOnClickListener {
            val intent = Intent(context, ZoomImageActivity::class.java)
            intent.putExtra("image", list[itemholder.layoutPosition].image)
            context?.startActivity(intent)
        }
        itemholder.img_recieve.setOnClickListener {
            val intent = Intent(context, ZoomImageActivity::class.java)
            intent.putExtra("image", list[itemholder.layoutPosition].image)
            context?.startActivity(intent)
        }


//        itemholder.itemView.setOnClickListener {
//            clicks.onitemclick()
//        }
    }


}