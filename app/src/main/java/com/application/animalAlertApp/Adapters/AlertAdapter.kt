package com.application.animalAlertApp.Adapters

import android.content.Context
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.application.animalAlertApp.Interfaces.AlertsOptionInterface
import com.application.animalAlertApp.Network.ApiService.Companion.IMAGE_BASE_URL
import com.application.animalAlertApp.R
import com.application.animalAlertApp.SharedPrefrences.MySharedPreferences
import com.application.animalAlertApp.Utils.changeformat
import com.application.animalAlertApp.data.Response.MessageX
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import de.hdodenhof.circleimageview.CircleImageView

class AlertAdapter(var context: Context?, private var list: List<MessageX>,
    val inteface: AlertsOptionInterface) : RecyclerView.Adapter<AlertAdapter.ViewHolder>() {
    val mySharedPreferences = MySharedPreferences(context!!)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.alertnew_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_name.setText(list.get(position).userName)
        holder.tv_descriprion.setText(list.get(position).addTitle)

        Glide.with(context!!).load(IMAGE_BASE_URL + list.get(position).userImage)
            .diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.placeholder)
            .into(holder.img_profile)

        if (list.get(position).priority.contentEquals("HIGH")) {
            holder.constraint.setBackgroundResource(R.drawable.alert_red_backg)
        } else if (list.get(position).priority.contentEquals("MEDIUM")) {
            holder.constraint.setBackgroundResource(R.drawable.alert_yellow_backg)
        } else if (list.get(position).priority.contentEquals("LOW")) {
            holder.constraint.setBackgroundResource(R.drawable.alert_background)
        }

        holder.tv_date.setText(changeformat(list.get(position).createdAt))

        holder.dots.setOnClickListener {
            if (list.get(position).userId.equals(myuserid())) {
                showOptionPopMenu(holder, list.get(position))
            } else {
                showOtherUserPopMenu(holder, list.get(position))
            }
        }

        holder.itemView.setOnClickListener {
            inteface.OnDetail(list.get(position)._id,list[position].userName)
        }
    }

    private fun myuserid(): String {
        return mySharedPreferences.getUserid()!!
    }


    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tv_name: TextView = itemView.findViewById(R.id.tv_name)
        val tv_closed: TextView = itemView.findViewById(R.id.tv_closed)
        val tv_descriprion: TextView = itemView.findViewById(R.id.tv_descriprion)
        val dots: ImageView = itemView.findViewById(R.id.dots)
        val constraint: ConstraintLayout = itemView.findViewById(R.id.constraint)
        val img_profile: CircleImageView = itemView.findViewById(R.id.img_profile)
        val tv_date: TextView = itemView.findViewById(R.id.tv_date)
    }


    fun setData(list: List<MessageX>) {
        this.list = list
        notifyDataSetChanged()
    }


    private fun showOptionPopMenu(
        holder: ViewHolder,
        selectedlist: MessageX
    ) {
        val popup = PopupMenu(context!!, holder.dots, Gravity.END, 0, R.style.MyPopupMenu)
        popup.inflate(R.menu.alert_popmenu)
        popup.show()

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.edit -> {
                    var petname=""
                    var petid=""
                    if (selectedlist.petName.isNullOrEmpty()){
                        Log.e("pet","null")
                    }else {
                        petname=selectedlist.petName
                        petid=selectedlist._id
                    }
                    inteface.onEdit(
                        holder.adapterPosition,
                        selectedlist._id,
                        selectedlist.addTitle,
                        petname,
                        selectedlist.priority,
                        selectedlist.description,
                        petid
                    )
                }
                R.id.close -> {
                    inteface.OnClosePost(holder.adapterPosition, selectedlist._id)
                }
                R.id.delete -> {
                    inteface.OnDelete(holder.adapterPosition, selectedlist._id)
                }
            }
            true
        })
    }

    private fun showOtherUserPopMenu(
        holder: ViewHolder,
        selectedlist: MessageX
    ) {
        val popup = PopupMenu(context!!, holder.dots, Gravity.END, 0, R.style.MyPopupMenu)
        popup.inflate(R.menu.chat_report_menu)
        popup.show()

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.chat_pop -> {
                    inteface.OnChat(holder.adapterPosition, selectedlist._id,selectedlist)
                }
                R.id.report -> {
                    inteface.OnReport(holder.adapterPosition, selectedlist._id)
                }
            }
            true
        })
    }
}