package com.application.animalAlertApp.View.Shops

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.viewpager.widget.ViewPager
import com.application.animalAlertApp.Adapters.BusinessProPagerAdapter
import com.application.animalAlertApp.SharedPrefrences.MySharedPreferences
import com.application.animalAlertApp.Utils.checkForInternet
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.databinding.ActivityCreateShopBinding
import dagger.hilt.android.AndroidEntryPoint
import com.application.animalAlertApp.View.Payments.ChoosePlanActivity


@AndroidEntryPoint
class CreateShopActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateShopBinding
    private var ScreenStatus: String = ""
    private val viewmodel: ShopViewModel by viewModels()
    private lateinit var prefrence: MySharedPreferences
    private var paymentstatus = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_create_shop)
        binding = ActivityCreateShopBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefrence = MySharedPreferences(this)
        ScreenStatus = intent.getStringExtra("FragmentType").toString()
        paymentstatus = intent.getIntExtra("paymentstatus", 0)
        setPager()

        if (checkForInternet(this)) {
            viewmodel.getbusinessdata(prefrence.getUserid()!!)
        } else {
            toast("No Internet connection")
        }


        binding.btBack.setOnClickListener {
            BackScreens()
        }

        val filter = IntentFilter()
        filter.addAction("ChangePaymentStatus")
        registerReceiver(reciver, filter)
    }

    val reciver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(reciver)
    }


    private fun setPager() {
        val adapter = BusinessProPagerAdapter(supportFragmentManager)
        adapter.addFragment(CreateShopFragement1(), "")
        adapter.addFragment(CreateShopFragment2(), "")
        adapter.addFragment(AddPortfolioFragment(), "")
        binding.viewpager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewpager)

        for (i in 0 until binding.tabLayout.getTabCount()) {
            val tab = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val p = tab.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(10, 0, 10, 0)
            tab.requestLayout()
        }


        binding.viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                Log.e("Pager", "Scrolled")
            }

            override fun onPageScrollStateChanged(state: Int) {
                Log.e("Pager", "Statechanged")
            }

            override fun onPageSelected(position: Int) {
                if (position == 0 || position == 1) {
                    binding.tvTitle.setText("Create Shop")
                } else if (position == 2) {
                    binding.tvTitle.setText("Add Portfolio")
                }
            }
        })

        if (ScreenStatus.equals("BussinessInfo")) {
            binding.viewpager.currentItem = 0
        } else if (ScreenStatus.equals("Service")) {
            binding.viewpager.currentItem = 1
        } else if (ScreenStatus.equals("Portfolio")) {
            binding.viewpager.currentItem = 2
        }
    }

    fun changescreens() {
        if (binding.viewpager.currentItem == 0) {
            binding.viewpager.currentItem = 1
        } else if (binding.viewpager.currentItem == 1) {
            binding.viewpager.currentItem = 2
        } else if (binding.viewpager.currentItem == 2) {
            if (paymentstatus.equals(1)) {
                finish()
            } else {
                val intent = Intent(this, ChoosePlanActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun BackScreens() {
        if (binding.viewpager.currentItem == 2) {
            binding.viewpager.currentItem = 1
        } else if (binding.viewpager.currentItem == 1) {
            binding.viewpager.currentItem = 0
        } else if (binding.viewpager.currentItem == 0) {
            finish()
        }
    }


    override fun onBackPressed() {
        // super.onBackPressed()
        BackScreens()
    }


}