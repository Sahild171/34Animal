package com.application.animalAlertApp.View.Shops

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.R
import com.application.animalAlertApp.Utils.checkForInternet
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.View.BusinessProfile.EditBussProfile.EditBusinessInfoActivity
import com.application.animalAlertApp.View.BusinessProfile.EditBussProfile.EditPortfolio
import com.application.animalAlertApp.View.BusinessProfile.EditBussProfile.EditServiceActivity
import com.application.animalAlertApp.View.Payments.CardDetailActivity
import com.application.animalAlertApp.data.Response.BusinessProfile.GetShop
import com.application.animalAlertApp.data.Response.BusinessProfile.Service
import com.application.animalAlertApp.databinding.ActivityCheckShopStatusBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class CheckShopStatus : AppCompatActivity() {
    private lateinit var binding: ActivityCheckShopStatusBinding
    private val viewmodel: ShopViewModel by viewModels()
    private lateinit var pd: Dialog
    private var _businessInfo: String = ""
    private var _serviceInfo: String = ""
    private var _paymentStatus: String = ""
    private var _portfolioStatus: String = ""
    private lateinit var list: ArrayList<GetShop>
    private lateinit var servicelist: ArrayList<Service>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckShopStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pd = MyProgressBar.progress(this)
        list = ArrayList()
        servicelist = ArrayList()
        //  setContentView(R.layout.activity_check_shop_status)
        checkstatus()

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.const1.setOnClickListener {
            if (_businessInfo.equals("0")) {
                val intent = Intent(this, CreateShopActivity::class.java)
                intent.putExtra("FragmentType", "BussinessInfo")
                startActivity(intent)
            } else {
                val intent = Intent(this, EditBusinessInfoActivity::class.java)
                intent.putExtra("name", list[0].businessName)
                intent.putExtra("location", list[0].location)
                intent.putExtra("mobile", list[0].mobileNo)
                intent.putExtra("description", list[0].businessDescription)
                startActivity(intent)
            }
        }

        binding.constraint2.setOnClickListener {
            if (_serviceInfo.equals("0")) {
                val intent = Intent(this, CreateShopActivity::class.java)
                intent.putExtra("FragmentType", "Service")
                startActivity(intent)
            } else {
                val intent = Intent(this, EditServiceActivity::class.java)
                val args = Bundle()
                args.putSerializable("service_list", `servicelist` as Serializable?)
                intent.putExtra("BUNDLE", args)
                startActivity(intent)
            }
        }

        binding.constraint3.setOnClickListener {
            if (_portfolioStatus.equals("null")) {
                val intent = Intent(this, CreateShopActivity::class.java)
                intent.putExtra("FragmentType", "Portfolio")
                startActivity(intent)
            }else {
                val intent = Intent(this, EditPortfolio::class.java)
                startActivity(intent)
            }
        }

        binding.payment.setOnClickListener {
            if (_paymentStatus.equals("0")) {
                if (_businessInfo.equals("0") || _serviceInfo.equals("0") || _portfolioStatus.equals(
                        "null"
                    )
                ) {
                    toast("Please Complete your steps before payment")
                } else {
                    val intent = Intent(this, CardDetailActivity::class.java)
                    startActivity(intent)
                }
            } else {
                val intent = Intent(this, CardDetailActivity::class.java)
                startActivity(intent)
            }
        }

        val filter = IntentFilter()
        filter.addAction("ChangeBusinessInfoStatus")
        val filter1 = IntentFilter()
        filter1.addAction("ChangeSerrviceStatus")
        val filter2 = IntentFilter()
        filter.addAction("ChangePortfolioStatus")
        val filter3 = IntentFilter()
        filter.addAction("ChangePaymentStatus")
        registerReceiver(reciver, filter)
        registerReceiver(reciver1, filter1)
        registerReceiver(reciver2, filter2)
        registerReceiver(reciver3, filter3)

    }

    override fun onResume() {
        super.onResume()
        if (checkForInternet(this)) {
            viewmodel.checkShopStatus()
        } else {
            toast("No Internet connection")
        }
    }

    val reciver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            binding.imgBussinfo.setImageResource(R.drawable.ic_editshop)
            binding.imgCheckbuss.setImageResource(R.drawable.ic_white_tick)
            _businessInfo = "1"
        }
    }
    val reciver1 = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            binding.imgService.setImageResource(R.drawable.ic_editshop)
            binding.imgCheckservice.setImageResource(R.drawable.ic_white_tick)
            _serviceInfo = "1"
        }
    }
    val reciver2 = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            binding.imgPortfoilo.setImageResource(R.drawable.ic_editshop)
            binding.imgCheckportfolio.setImageResource(R.drawable.ic_white_tick)
            _portfolioStatus = "1"
        }
    }
    val reciver3 = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            _paymentStatus = "1"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(reciver)
        unregisterReceiver(reciver1)
        unregisterReceiver(reciver2)
    }

    private fun checkstatus() {
        viewmodel.checkshopstatus.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        pd.dismiss()
                        _paymentStatus = "" + it.value.paymentStatus
                        _businessInfo = "" + it.value.shopStatus
                        _serviceInfo = "" + it.value.serviceStatus
                        _portfolioStatus = "" + it.value.logoStatus
                        list.clear()
                        servicelist.clear()
                        list.addAll(it.value.shopData)
                        servicelist.addAll(it.value.serviceData)
                        setdata()
                    } else {
                        //  pd.dismiss()
                        toast("" + it.value.message)
                    }
                }
                is Resource.Failure -> {
                    toast("" + it.exception!!.message)
                    //  pd.dismiss()
                }
                is Resource.Loading -> {
                    // pd.show()
                }
            }
        })
    }

    private fun setdata() {
        if (_businessInfo.equals("0")) {
            binding.imgBussinfo.setImageResource(R.drawable.green_add)
        } else {
            binding.imgBussinfo.setImageResource(R.drawable.ic_editshop)
            binding.imgCheckbuss.setImageResource(R.drawable.ic_white_tick)
        }

        if (_serviceInfo.equals("0")) {
            binding.imgService.setImageResource(R.drawable.green_add)
        } else {
            binding.imgService.setImageResource(R.drawable.ic_editshop)
            binding.imgCheckservice.setImageResource(R.drawable.ic_white_tick)
        }

        if (_portfolioStatus.equals("null")) {
            binding.imgPortfoilo.setImageResource(R.drawable.green_add)
        } else {
            binding.imgPortfoilo.setImageResource(R.drawable.ic_editshop)
            binding.imgCheckportfolio.setImageResource(R.drawable.ic_white_tick)
        }
    }

}