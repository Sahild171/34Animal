package com.application.animalAlertApp.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.R
import com.application.animalAlertApp.Utils.changeformat
import com.application.animalAlertApp.View.Home.Comments.PostCommentsActivity
import com.application.animalAlertApp.View.Profile.MyProfileActivity
import com.application.animalAlertApp.View.Requsts.ProfileActivity
import com.application.animalAlertApp.data.Response.Post.FindPost
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import android.content.Intent.ACTION_SEND
import com.application.animalAlertApp.Interfaces.PaginationAdapterCallback
import com.application.animalAlertApp.Interfaces.PostInterface
import com.application.animalAlertApp.databinding.AlertItemsBinding


class HomeAdapter(
    val context: Context?,
    var arraylist: List<FindPost>,
    val myuserid: String,
    val postlike: PostInterface
) : RecyclerView.Adapter<HomeAdapter.ViewHolder>(), PaginationAdapterCallback {
    lateinit var viewPagerImageAdapter: ViewPagerImageAdapter


    inner class ViewHolder(var binding: AlertItemsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AlertItemsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvName.setText(arraylist[position].userName)
        holder.binding.textView7.setText(changeformat(arraylist[position].createdAt))
        holder.binding.textView8.setText(arraylist[position].description)

        if (arraylist[position].like == 0) {
            holder.binding.imgLike.setImageDrawable(context?.resources?.getDrawable(R.drawable.likes))
        } else if (arraylist[position].like == 1) {
            holder.binding.imgLike.setImageDrawable(context?.resources?.getDrawable(R.drawable.filled_like))
        }

        if (arraylist[position].likeCount == 0) {
            holder.binding.tvLikes.visibility = View.INVISIBLE
            holder.binding.tvLikeheading.visibility = View.INVISIBLE
        } else {
            holder.binding.tvLikes.visibility = View.VISIBLE
            holder.binding.tvLikeheading.visibility = View.VISIBLE
            holder.binding.tvLikes.setText("" + arraylist[position].likeCount)
        }

        Glide.with(context!!)
            .load(ApiService.IMAGE_BASE_URL + arraylist[position].userProfile)
            .diskCacheStrategy(
                DiskCacheStrategy.ALL
            ).placeholder(R.drawable.placeholder).into(holder.binding.imgProfile)

        setViewImageAdapter(holder, arraylist[position].postImg)
        holder.binding.imgProfile.setOnClickListener {
            if (myuserid.equals(arraylist[position].userId)) {
                val intent = Intent(context, MyProfileActivity::class.java)
                context.startActivity(intent)
            } else {
                val intent = Intent(context, ProfileActivity::class.java)
                intent.putExtra("userid", arraylist[position].userId)
                context.startActivity(intent)
            }
        }

        holder.binding.imgComments.setOnClickListener {
            val intent = Intent(context, PostCommentsActivity::class.java)
            intent.putExtra("postid", arraylist[position]._id)
            context.startActivity(intent)
        }

        holder.binding.imgShare.setOnClickListener {
            share()
        }


        holder.binding.imgLike.setOnClickListener {
            val likestatus = arraylist[position].like
            if (likestatus == 0) {
                arraylist[position].like = 1
                val likecount = holder.binding.tvLikes.text.toString()
                var likess = likecount.toInt()
                likess++
                holder.binding.tvLikes.setText("" + likess + "")
                holder.binding.tvLikes.visibility = View.VISIBLE
                holder.binding.tvLikeheading.visibility = View.VISIBLE
                holder.binding.imgLike.setImageDrawable(context.resources.getDrawable(R.drawable.filled_like))
            } else if (likestatus == 1) {
                arraylist[position].like = 0
                val likecount = holder.binding.tvLikes.text.toString()
                var likess = likecount.toInt()
                likess--
                holder.binding.tvLikes.setText("" + likess + "")
                holder.binding.tvLikes.visibility = View.VISIBLE
                holder.binding.tvLikeheading.visibility = View.VISIBLE
                holder.binding.imgLike.setImageDrawable(context.resources.getDrawable(R.drawable.likes))
                if (likess == 0) {
                    holder.binding.tvLikes.visibility = View.INVISIBLE
                    holder.binding.tvLikeheading.visibility = View.INVISIBLE
                }
            }
            postlike.onPostLike(position, arraylist[position]._id)
        }

//        if (!arraylist[position].perference.isNullOrEmpty()) {
//            if (arraylist[position].perference.size > 0) {
//                setprofileborder(arraylist[position].perference.toString(), holder)
//            }
//        }
    }

    fun setViewImageAdapter(
        holder: ViewHolder,
        lessons: List<String>
    ) {
        viewPagerImageAdapter = ViewPagerImageAdapter(context, lessons, holder.binding.viewPagerImage)
        holder.binding.viewPagerImage.setAdapter(viewPagerImageAdapter)
    }

    override fun getItemCount(): Int {
        return arraylist.size
    }


    fun setData(arraylist: List<FindPost>) {
        this.arraylist = arraylist
        notifyDataSetChanged()
    }


    private fun share() {
        val sharingIntent = Intent(ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(
            Intent.EXTRA_TEXT, """AnimalAlert Community :Download now"""
        )
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context!!.startActivity(Intent.createChooser(sharingIntent, "Share via"))
    }

    override fun retryPageLoad() {

    }


    private fun setprofileborder(data: String, h: ViewHolder) {
        if (data.contains("petowner") && data.contains("petlover") && data.contains("volunteer")) {
            h.binding.circleProgressview.setBackgroundResource(R.drawable.three_color)
        } else if (data.contains("petowner") && data.contains("petlover")) {
            h.binding.circleProgressview.setBackgroundResource(R.drawable.two_color)
        } else if (data.contains("petlover") && data.contains("volunteer")) {
            h.binding.circleProgressview.setBackgroundResource(R.drawable.orangle_purple)
        } else if (data.contains("petowner") && data.contains("volunteer")) {
            h.binding.circleProgressview.setBackgroundResource(R.drawable.green_purple)
        } else if (data.contains("petowner") && !data.contains("petlover") && !data.contains("volunteer")) {
            h.binding.circleProgressview.setBackgroundResource(R.drawable.one_color)
        } else if (!data.contains("petowner") && data.contains("petlover") && !data.contains(
                "volunteer"
            )
        ) {
            h.binding.circleProgressview.setBackgroundResource(R.drawable.orange_color)
        } else if (!data.contains("petowner") && !data.contains("petlover") && data.contains(
                "volunteer"
            )
        )
            h.binding.circleProgressview.setBackgroundResource(R.drawable.purple_color)
        else {
            h.binding.circleProgressview.setBackgroundResource(R.drawable.one_color)
        }

    }
}