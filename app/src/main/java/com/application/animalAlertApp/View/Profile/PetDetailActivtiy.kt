package com.application.animalAlertApp.View.Profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.application.animalAlertApp.Adapters.ShopDetailPagerAdapter
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.View.AddAlerts.MyPetsState
import com.application.animalAlertApp.data.Response.MessageXX
import com.application.animalAlertApp.databinding.ActivityPetDetailActivtiyBinding
import com.google.android.material.tabs.TabLayoutMediator


class PetDetailActivtiy : AppCompatActivity() {
    private lateinit var binding: ActivityPetDetailActivtiyBinding
    private val viewModel: PetDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_pet_detail_activtiy)
        binding = ActivityPetDetailActivtiyBinding.inflate(layoutInflater)
        setContentView(binding.root)
      //   getmypets()
    }


    private fun getmypets() {
        viewModel.getpets()
        viewModel.mypetData.observe(this, Observer {
            binding.apply {
                when (it) {
                    is MyPetsState.Success -> {
                        if (it.data.status == 200) {
                            setData(it.data.findPet)
                            setViewPager(it.data.findPet[0].petImages)
                        }
                    }
                    is MyPetsState.Failure -> {
                        toast("" + it.msg)
                        Log.e("mypet", "" + it.msg)
                    }
                    is MyPetsState.Loading -> {
                        //    progress.show()
                    }
                    is MyPetsState.Empty -> {

                    }
                }
            }
        })
    }


    private fun setData(findAlert: List<MessageXX>) {
        binding.tvName.setText(findAlert[0].petName)
        //  binding.tvDescription.setText(findAlert.)
        binding.tvColor.setText(findAlert[0].petColor)

    }


    private fun setViewPager(petimages: List<String>) {
        if (petimages.size == 0) {
            binding.tvNoimage.visibility = View.VISIBLE
        }
        val adapter = ShopDetailPagerAdapter(this)
        adapter.setItem(petimages)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
        }.attach()
    }


}