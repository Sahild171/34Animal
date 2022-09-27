package com.application.animalAlertApp.Adapters

import android.content.Context
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import android.widget.TextView
import android.view.LayoutInflater
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.asLiveData
import com.application.animalAlertApp.Model.InfoModel
import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.R
import com.application.animalAlertApp.SharedPrefrences.UserPreferences
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import de.hdodenhof.circleimageview.CircleImageView


class InfoWndowAdapter(val  context: Context):GoogleMap.InfoWindowAdapter {


    override fun getInfoContents(p0: Marker): View? {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v: View = inflater.inflate(R.layout.custom_marker_layout, null)
        val image = v.findViewById<View>(R.id.user_dp) as CircleImageView
        val userlocation = v.findViewById<View>(R.id.tv_location) as TextView
      //  image.setImageResource(R.mipmap.girll)
        Glide.with(context).load(p0.snippet).diskCacheStrategy(
            DiskCacheStrategy.ALL
        ).placeholder(R.drawable.placeholder).into(image)
        userlocation.setText(p0.title)
        return v
    }

    override fun getInfoWindow(p0: Marker): View? {
        return null
    }
}