package com.application.animalAlertApp.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.R
import com.application.animalAlertApp.View.Requsts.ProfileActivity
import com.application.animalAlertApp.data.Response.Profile.SearchUser
import com.application.animalAlertApp.databinding.SearchItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class SearchUserAdapter(val context: Context, var searchlist: ArrayList<SearchUser>) :
    RecyclerView.Adapter<SearchUserAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: SearchItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            SearchItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    fun setData(searchlist: ArrayList<SearchUser>) {
        this.searchlist = searchlist
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvName.setText(searchlist[position].name)
        Glide.with(context).load(ApiService.IMAGE_BASE_URL + searchlist[position].image)
            .diskCacheStrategy(
                DiskCacheStrategy.ALL
            ).placeholder(R.drawable.placeholder).into(holder.binding.imgProfile)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra("userid", searchlist[position]._id)
            context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return searchlist.size
    }
}