package com.application.animalAlertApp.View.Shops

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.application.animalAlertApp.Adapters.ShopAdapter
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.R
import com.application.animalAlertApp.SharedPrefrences.MySharedPreferences
import com.application.animalAlertApp.Utils.checkForInternet
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.data.Response.Shop.GetShop
import com.application.animalAlertApp.databinding.FragmentShopBinding
import com.google.android.gms.ads.*
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class ShopFragment : Fragment() {
    private lateinit var adapter: ShopAdapter
    private lateinit var binding: FragmentShopBinding
    private val viewModel: ShopViewModel by viewModels()
    private lateinit var list: ArrayList<GetShop>
    private lateinit var sharedPreferences: MySharedPreferences
    private lateinit var pd: Dialog
    private var paymentstatus = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentShopBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = MySharedPreferences(requireContext())
        list = ArrayList()
        pd = MyProgressBar.progress(context)
        adapter = ShopAdapter(context, list)
        binding.recyclerview.adapter = adapter
        ads()
        shopdata()
        getAllshops()
        checkstatus()

        binding.swipeRefresh.setOnRefreshListener(refresh)


        binding.imgAdd.setOnClickListener {
            if (checkForInternet(requireContext())) {
                viewModel.checkShopStatus()
            } else {
                context?.toast("No Internet connection")
            }
        }

        binding.closeadd.setOnClickListener {
            binding.adView.visibility=View.GONE
            binding.closeadd.visibility=View.GONE
        }

    }


    private fun ads() {
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(requireActivity()) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        binding.adView.adListener = object: AdListener() {
            override fun onAdClicked() {
                Log.e("Admob","Clicked")
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                Log.e("Admob","Closed")
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                Log.e("Admob",""+adError.message)
                // Code to be executed when an ad request fails.
            }

            override fun onAdImpression() {
                Log.e("Admob","admipression")
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            override fun onAdLoaded() {
                Log.e("Admob","Loaded")
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdOpened() {
                Log.e("Admob","Opened")
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        }

    }

    private fun checkstatus() {
        viewModel.checkshopstatus.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        pd.dismiss()
                        //Screen Navigation
                        if (it.value.shopStatus == 0 || it.value.serviceStatus == 0) {
                            val intent = Intent(context, CreateShopActivity::class.java)
                            intent.putExtra("paymentstatus", paymentstatus)
                            context?.startActivity(intent)
                        } else {
                            val intent = Intent(context, CheckShopStatus::class.java)
                            context?.startActivity(intent)
                        }
                    } else {
                        pd.dismiss()
                        context?.toast("" + it.value.message)
                    }
                }
                is Resource.Failure -> {
                    context?.toast("" + it.exception!!.message)
                    pd.dismiss()
                }
                is Resource.Loading -> {
                    pd.show()
                }
            }
        })
    }


    private val refresh = object : SwipeRefreshLayout.OnRefreshListener {
        override fun onRefresh() {
            getAllshops()
        }
    }


    private fun shopdata() {
        viewModel.allshopdata.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    list.clear()
                    binding.shimmer.stopShimmer()
                    binding.shimmer.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    if (it.value.status == 200) {
                        list.addAll(it.value.getShop)
                        if (list.size > 0) {
                            adapter.setdata(list)
                        } else {
                            context?.toast("No Data Found")
                        }
                        paymentstatus = it.value.paymentStatus

                        if (it.value.shopStatus == 1 || it.value.serviceStatus == 1) {
                            binding.imgAdd.visibility = View.GONE
                        } else {
                            binding.imgAdd.visibility = View.VISIBLE
                        }
                        if (it.value.paymentStatus.equals(0)) {
                            binding.imgAdd.visibility = View.VISIBLE
                        }

                        if (!it.value.subs_expiry_date.equals("null")) {

                            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                            val calendar1 = Calendar.getInstance()
                            val calendar2 = Calendar.getInstance()

                            val date1 = dateFormat.format(Date())
                            val datee = dateFormat.parse(date1)

                            val date2 = dateFormat.parse(it.value.subs_expiry_date)
                            calendar1.time = datee
                            calendar2.time = date2


                            if (calendar1.compareTo(calendar2).equals(0)) {
                                binding.imgAdd.visibility = View.VISIBLE
                            } else if (calendar1.compareTo(calendar2).equals(-1)) {
                                binding.imgAdd.visibility = View.GONE
                            } else if (calendar1.compareTo(calendar2).equals(1)) {
                                binding.imgAdd.visibility = View.VISIBLE
                            } else {
                                binding.imgAdd.visibility = View.VISIBLE
                            }
                        }

                    }
                }
                is Resource.Failure -> {
                    context?.toast("" + it.exception?.message)
                    binding.shimmer.stopShimmer()
                    binding.shimmer.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                }
                is Resource.Loading -> {
                    binding.shimmer.startShimmer()
                    binding.imgAdd.visibility = View.GONE
//                    binding.swipeRefresh.isRefreshing=false
                }
            }
        })
    }


    private fun getAllshops() {
        if (checkForInternet(requireContext())) {
            viewModel.getAllshops()
        } else {
            context?.toast("No internet connecction")
        }
    }


}