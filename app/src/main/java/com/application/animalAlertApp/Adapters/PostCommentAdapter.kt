package com.application.animalAlertApp.Adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.animalAlertApp.Interfaces.CommentInterface
import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.R
import com.application.animalAlertApp.View.Home.Comments.PostCommentsActivity
import com.application.animalAlertApp.data.Response.Comments.FindComment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import android.graphics.Paint
import androidx.core.content.res.ResourcesCompat
import com.application.animalAlertApp.databinding.CommentsItemsBinding


class PostCommentAdapter(
    val context: PostCommentsActivity,
    var list: List<FindComment>, val commentinterface: CommentInterface
) : RecyclerView.Adapter<PostCommentAdapter.ViewHolder>() {
    private lateinit var adapter: CommentReplyAdpater


    inner class ViewHolder(val binding: CommentsItemsBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CommentsItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvUsername.setText(list[position].userName1)
        holder.binding.tvComment.setText(list[position].comment)
        Glide.with(context).load(ApiService.IMAGE_BASE_URL + list[position].image)
            .diskCacheStrategy(
                DiskCacheStrategy.ALL
            ).placeholder(R.drawable.placeholder).into(holder.binding.circleImageView4)


        if (list[position].replyArray.size == 0) {
            holder.binding.tvReply.visibility = View.GONE
        } else {
            holder.binding.tvReply.visibility = View.VISIBLE
            adapter = CommentReplyAdpater(context, list[position].replyArray)
            holder.binding.recyclerview.adapter = adapter
        }


        if (list[position].likeCount == 0) {
            holder.binding.tvLikecount.visibility = View.GONE
        } else {
            holder.binding.tvLikecount.visibility = View.VISIBLE
            holder.binding.tvLikecount.setText("" + list[position].likeCount)
        }


//        if (list[position].like == 1) {
//            val typeface = ResourcesCompat.getFont(context, R.font.poppins_semibold)
//            holder.tv_like.setTypeface(typeface)
//        } else {
//            val typeface = ResourcesCompat.getFont(context, R.font.poppins)
//            holder.tv_like.setTypeface(typeface)
//        }

        var viewreply = false
        if (list[position].visibilitystatus) {
            holder.binding.recyclerview.visibility = View.VISIBLE
            viewreply = true
            holder.binding.tvReply.setText(context.resources.getString(R.string.hide_reply))
            holder.binding.tvReply.setPaintFlags(holder.binding.tvReply.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
        } else {
            holder.binding.recyclerview.visibility = View.GONE
        }


        holder.binding.tvReply.setOnClickListener {
            if (list[position].replyArray.size > 0) {
                if (viewreply) {
                    holder.binding.tvReply.setText(context.resources.getString(R.string.view_reply))
                    holder.binding.tvReply.setPaintFlags(holder.binding.tvReply.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
                    holder.binding.recyclerview.visibility = View.GONE
                    viewreply = false
                } else {
                    holder.binding.recyclerview.visibility = View.VISIBLE
                    holder.binding.tvReply.setText(context.resources.getString(R.string.hide_reply))
                    holder.binding.tvReply.setPaintFlags(holder.binding.tvReply.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
                    viewreply = true
                }
            }
        }

        var likestatus = list[position].like
        if (likestatus == 1) {
            holder.binding.tvLike.setText("Liked")
            holder.binding.tvLike.setTextColor(context.resources.getColor(R.color.green))
            val typeface = ResourcesCompat.getFont(context, R.font.poppins_semibold)
            holder.binding.tvLike.setTypeface(typeface)
            holder.binding.tvLikecount.setTextColor(context.resources.getColor(R.color.green))
            holder.binding.tvLikecount.setTypeface(typeface)
        } else {
            holder.binding.tvLike.setText("Like")
            holder.binding.tvLike.setTextColor(context.resources.getColor(R.color.quantum_grey))
            val typeface = ResourcesCompat.getFont(context, R.font.poppins_semibold)
            holder.binding.tvLike.setTypeface(typeface)
            holder.binding.tvLikecount.setTextColor(context.resources.getColor(R.color.quantum_grey))
            holder.binding.tvLikecount.setTypeface(typeface)
        }

        holder.binding.tvLike.setOnClickListener {
            if (likestatus == 0) {
                likestatus = 1
                holder.binding.tvLike.setText("Liked")
                val likecount = holder.binding.tvLikecount.text.toString()
                holder.binding.tvLike.setTextColor(context.resources.getColor(R.color.green))
                val typeface = ResourcesCompat.getFont(context, R.font.poppins_semibold)
                holder.binding.tvLike.setTypeface(typeface)
                holder.binding.tvLikecount.setTextColor(context.resources.getColor(R.color.green))
                holder.binding.tvLikecount.setTypeface(typeface)
                var finalcount = likecount.toInt()
                finalcount++
                holder.binding.tvLikecount.setText("" + finalcount)
                commentinterface.onLikeComment(list[position]._id)
                holder.binding.tvLikecount.visibility = View.VISIBLE
            } else {
                likestatus = 0
                holder.binding.tvLike.setText("Like")
                val likecount = holder.binding.tvLikecount.text.toString()
                var finalcount = likecount.toInt()
                holder.binding.tvLike.setTextColor(context.resources.getColor(R.color.quantum_grey))
                val typeface = ResourcesCompat.getFont(context, R.font.poppins)
                holder.binding.tvLike.setTypeface(typeface)
                holder.binding.tvLikecount.setTextColor(context.resources.getColor(R.color.quantum_grey))
                holder.binding.tvLikecount.setTypeface(typeface)
                finalcount--
                holder.binding.tvLikecount.setText("" + finalcount)
                commentinterface.onLikeComment(list[position]._id)
            }
        }


        holder.binding.addReply.setOnClickListener {
            commentinterface.onReplyComment(list[position]._id, list[position].userName1, position)
        }

    }


    fun setData(arraylist: List<FindComment>) {
        this.list = arraylist
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return list.size
    }

}