package com.application.animalAlertApp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.R
import com.application.animalAlertApp.data.Response.Comments.ReplyComments
import com.application.animalAlertApp.databinding.CommentsReplyItemsBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class CommentReplyAdpater(val context: Context, val list: List<ReplyComments>) :
    RecyclerView.Adapter<CommentReplyAdpater.ViewHolder>() {

    inner class ViewHolder(var binding: CommentsReplyItemsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CommentsReplyItemsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvComment.setText(list[position].reply)
        holder.binding.tvUsername.setText(list[position].name)
        Glide.with(context).load(ApiService.IMAGE_BASE_URL + list[position].image)
            .diskCacheStrategy(
                DiskCacheStrategy.ALL
            ).placeholder(R.drawable.placeholder).into(holder.binding.imgProfileReply)


        holder.itemView.setOnLongClickListener {
            // holder.binding.main.setBackgroundColor(context.resources.getColor(R.color.quantum_grey))
            true
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }
}