package com.application.animalAlertApp.View.Chats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.application.animalAlertApp.databinding.ActivityZoomImageBinding
import com.bumptech.glide.Glide

class ZoomImageActivity : AppCompatActivity() {
    private lateinit var binding:ActivityZoomImageBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_zoom_image)
        binding = ActivityZoomImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val iamge=intent.getStringExtra("image")
        Glide.with(this).load(iamge).into(binding.image)




    }
}