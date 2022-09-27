package com.application.animalAlertApp.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.application.animalAlertApp.R
import java.util.ArrayList
import com.application.animalAlertApp.Adapters.HomeOffersAdapter
import com.application.animalAlertApp.Model.ImageModel
import com.application.animalAlertApp.View.Auth.LoginActivity
import com.application.animalAlertApp.View.RegisterUser.SignUpActivity
import com.application.animalAlertApp.databinding.ActivityWelcomeBinding
import com.google.android.material.tabs.TabLayoutMediator


class WelcomeActivity : AppCompatActivity() {
    var arrayList: ArrayList<ImageModel>? = null
    private lateinit var binding:ActivityWelcomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_welcome)
        binding= ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setViewPager()

        binding.btNext.setOnClickListener {
            GoToNext()
        }
    }

    private fun setViewPager() {
       arrayList=ArrayList()

        val imagemode = ImageModel(R.drawable.walking1, resources.getString(R.string.walk1))
        arrayList?.add(imagemode)
        val imagemode1 = ImageModel(R.drawable.walking2, resources.getString(R.string.walk2))
        arrayList?.add(imagemode1)
        val imagemodel2 = ImageModel(R.mipmap.walk, resources.getString(R.string.walk3))
        arrayList?.add(imagemodel2)

        val adapter = HomeOffersAdapter()
        adapter.setItem(arrayList!!)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
        }.attach()

    }


    private fun GoToNext() {
        if (binding.viewPager.currentItem == 0) {
            binding.viewPager.setCurrentItem(1)
        } else if (binding.viewPager.currentItem == 1) {
            binding.viewPager.setCurrentItem(2)
        } else if (binding.viewPager.currentItem == 2) {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}