package com.application.animalAlertApp.View.Home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.application.animalAlertApp.Adapters.HomeAdapter
import com.application.animalAlertApp.Interfaces.PostInterface
import com.application.animalAlertApp.Model.AlertModel
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.SharedPrefrences.MySharedPreferences
import com.application.animalAlertApp.Utils.PaginationScrollListener
import com.application.animalAlertApp.Utils.checkForInternet
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.View.Post.AddPostActivity
import com.application.animalAlertApp.data.Response.Post.FindPost
import com.application.animalAlertApp.databinding.FragmentHomeBinding
import com.google.android.gms.ads.*
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.ArrayList

@AndroidEntryPoint
class HomeFragment : Fragment(), PostInterface {
    private lateinit var adapter: HomeAdapter
     private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var prefrence: MySharedPreferences
    private val pageStart: Int = 1
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false
    private var totalPages: Int = 1
    private var currentPage: Int = pageStart
    private lateinit var list: ArrayList<FindPost>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefrence = MySharedPreferences(requireContext())
        list = ArrayList()
        adapter = HomeAdapter(context, list, prefrence.getUserid()!!, this)
        binding.recyclerview.adapter = adapter
        getallpost()
        ads()

        binding.swipeRefresh.setOnRefreshListener(refresh)

        binding.imgAdd.setOnClickListener {
            val intent = Intent(context, AddPostActivity::class.java)
            startActivity(intent)
        }
        getfirstpagePost()

        binding.recyclerview.addOnScrollListener(object :
            PaginationScrollListener(binding.recyclerview.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1
                Handler(Looper.myLooper()!!).postDelayed({
                    if (checkForInternet(requireContext())) {
                        viewModel.getAllPosts(currentPage)
                    } else {
                        context?.toast("No Internet Connection")
                    }
                }, 1000)
            }
            override fun getTotalPageCount(): Int {
                return totalPages
            }
            override fun isLastPage(): Boolean {
                return isLastPage
            }
            override fun isLoading(): Boolean {
                return isLoading
            }
        })

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


    private val refresh = object : SwipeRefreshLayout.OnRefreshListener {
        override fun onRefresh() {
            currentPage = pageStart
            if (checkForInternet(requireContext())) {
                viewModel.getfirstPosts(currentPage)
            } else {
                context?.toast("No Internet Connection")
            }
        }
    }


    private fun getfirstpagePost() {
        viewModel.firstpostdata.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        list.clear()
                        list.addAll(it.value.findPost)

                        adapter.setData(list)
                        totalPages = it.value.countPost
                        Log.e("count",""+it.value.countPost)
                        if (currentPage <= totalPages)

                        //  adapter.addLoadingFooter()
                        else
                            isLastPage = true

                        if (binding.progressbar.isVisible) {
                            binding.progressbar.visibility = View.GONE
                        }
                        binding.shimmer.stopShimmer()
                        binding.shimmer.visibility = View.GONE
                        binding.swipeRefresh.isRefreshing = false
                        binding.recyclerview.visibility = View.VISIBLE
                    } else {
                        activity?.toast("" + it.value.message)
                        binding.swipeRefresh.isRefreshing = false
                        binding.recyclerview.visibility = View.VISIBLE
                    }
                }
                is Resource.Failure -> {
                    activity?.toast("" + it.exception!!.message)
                    binding.shimmer.stopShimmer()
                    binding.shimmer.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    binding.recyclerview.visibility = View.VISIBLE
                }
                is Resource.Loading -> {
                    if (currentPage == 1) {
                        isLoading=false
                        binding.recyclerview.visibility = View.INVISIBLE
                        binding.shimmer.startShimmer()
                        binding.shimmer.visibility = View.VISIBLE
                        binding.swipeRefresh.isRefreshing = false
                    } else {
                        binding.progressbar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun getallpost() {
        if (checkForInternet(requireContext())) {
            viewModel.getAllPosts(currentPage)
        } else {
            context?.toast("No Internet Connection")
        }

        viewModel.mypostdata.observe(requireActivity(), Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        list.addAll(it.value.findPost)
                        adapter.setData(list)
                        totalPages = it.value.countPost

                        if (currentPage <= totalPages)

                        //  adapter.addLoadingFooter()
                        else
                            isLastPage = true

                        if (binding.progressbar.isVisible) {
                            binding.progressbar.visibility = View.GONE
                        }
                        binding.shimmer.stopShimmer()
                        binding.shimmer.visibility = View.GONE
                        binding.swipeRefresh.isRefreshing = false
                        binding.recyclerview.visibility = View.VISIBLE
                    } else {
                        activity?.toast("" + it.value.message)
                        binding.swipeRefresh.isRefreshing = false
                        binding.recyclerview.visibility = View.VISIBLE
                    }
                }
                is Resource.Failure -> {
                    activity?.toast("" + it.exception!!.message)
                    binding.shimmer.stopShimmer()
                    binding.shimmer.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    binding.recyclerview.visibility = View.VISIBLE
                }
                is Resource.Loading -> {
                    if (currentPage == 1) {
                        binding.recyclerview.visibility = View.INVISIBLE
                        binding.shimmer.startShimmer()
                        binding.shimmer.visibility = View.VISIBLE
                        binding.swipeRefresh.isRefreshing = false
                    } else {
                        binding.progressbar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    override fun onPostLike(pos: Int, postId: String) {
        postlike(postId)
    }


    private fun postlike(post_id: String) {
        viewModel.postlike(post_id)
        viewModel.mypostlikedata.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        context?.toast(""+ it.value.message)
                    } else {
                        context?.toast("" + it.value.message)
                    }
                }
                is Resource.Failure -> {
                    context?.toast("" + it.exception!!.message)
                }
                is Resource.Loading -> {

                }
            }
        })
    }
}