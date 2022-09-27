package com.application.animalAlertApp.View.AlertDetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.application.animalAlertApp.Adapters.ShopDetailPagerAdapter
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.data.Response.FindAlertXX
import com.application.animalAlertApp.databinding.ActivityShopDetailBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class ShopDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShopDetailBinding
    private val viewmodel: AlertDetailViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_shop_detail)
        binding = ActivityShopDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val alertid = intent.getStringExtra("alertid").toString()
        val name= intent.getStringExtra("username").toString()
        binding.tvHeader.setText(name)
        getAlertDetail(alertid)
    }

    private fun getAlertDetail(alertid: String?) {
        viewmodel.getAlertdetail(alertid!!)
        lifecycleScope.launchWhenStarted {
            viewmodel.alertData.collect {
                binding.apply {
                    when (it) {
                        is AlertDetailState.Success -> {
                            // progress.dismiss()
                            if (it.data.status == 200) {
                                setData(it.data.findAlert)
                                setViewPager(it.data.findAlert.petImages)
                                shimmer.stopShimmer()
                                shimmer.visibility = View.GONE
                                showlabel()
                                if (it.data.petVisibility.equals(0)) {
                                    binding.viewPager.visibility = View.GONE
                                    binding.textView21.visibility = View.GONE
                                    binding.textView25.visibility = View.GONE
                                    binding.textView23.visibility = View.GONE
                                    binding.textView27.visibility = View.GONE
                                    binding.tvName.visibility = View.GONE
                                    binding.tvDescription.visibility = View.GONE
                                    binding.tvColor.visibility = View.GONE
                                    binding.tvBreed.visibility = View.GONE
                                    binding.tvNoimage.visibility=View.GONE
                                    binding.tabLayout.visibility=View.GONE
                                }
                            }
                        }
                        is AlertDetailState.Failure -> {
                            // progress.dismiss()
                            toast("" + it.msg)
                            shimmer.stopShimmer()
                            shimmer.visibility = View.GONE
                        }
                        is AlertDetailState.Loading -> {
                            //    progress.show()
                            shimmer.startShimmer()
                            hideAllLabels()
                            shimmer.visibility = View.VISIBLE
                        }
                        is AlertDetailState.Empty -> {
                            toast("Empty")
                            shimmer.stopShimmer()
                            shimmer.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun setData(findAlert: FindAlertXX) {
      //  binding.tvHeader.setText(findAlert.)
        binding.tvName.setText(findAlert.petName)
        binding.tvDescription.setText(findAlert.petDescription)
        binding.tvColor.setText(findAlert.petColor)
        binding.tvTitle.setText(findAlert.addTitle)
        binding.tvAlertDesc.setText(findAlert.description)
        binding.tvBreed.setText(findAlert.petBreed)
        binding.tvAlertPriority.setText(findAlert.priority)
    }

    private fun setViewPager(petimages: List<String>) {
        if (petimages != null) {
            if (petimages.size == 0) {
                binding.tvNoimage.visibility = View.VISIBLE
            }
        }
        Log.e("petimages", "" + petimages)
        val adapter = ShopDetailPagerAdapter(this)
        binding.viewPager.adapter = adapter
        if (petimages != null) {
            adapter.setItem(petimages)

            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            }.attach()
        }
    }

    fun CloseDetail(view: android.view.View) {
        finish()
    }


    fun hideAllLabels() {
        binding.textView21.visibility = View.INVISIBLE
        binding.textView25.visibility = View.INVISIBLE
        binding.textView23.visibility = View.INVISIBLE
        binding.textView27.visibility = View.INVISIBLE
        binding.textView28.visibility = View.INVISIBLE
        binding.textView29.visibility = View.INVISIBLE
    }

    fun showlabel() {
        binding.textView21.visibility = View.VISIBLE
        binding.textView25.visibility = View.VISIBLE
        binding.textView23.visibility = View.VISIBLE
        binding.textView27.visibility = View.VISIBLE
        binding.textView28.visibility = View.VISIBLE
        binding.textView29.visibility = View.VISIBLE
    }
}