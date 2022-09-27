package com.application.animalAlertApp.View.Notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.application.animalAlertApp.Adapters.NotificationsAdapter
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.data.Response.GetNotification
import com.application.animalAlertApp.databinding.FragmentNotificationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment : Fragment() {
    private val viewmodel: NotificationViewModel by viewModels()
    private lateinit var binding: FragmentNotificationBinding
    private lateinit var adapter: NotificationsAdapter
    private lateinit var arrayList: ArrayList<GetNotification>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arrayList = ArrayList()
        adapter = NotificationsAdapter(context, arrayList)
        binding.recyclerview.adapter = adapter
        binding.swipeRefresh.setOnRefreshListener(refresh)
        setData()
    }


    private val refresh = object : SwipeRefreshLayout.OnRefreshListener {
        override fun onRefresh() {
            setData()
        }
    }

    private fun setData() {
        viewmodel.getallnotification()
        viewmodel.notificationdata.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        arrayList.clear()
                        arrayList.addAll(it.value.getNotification)
                        if (arrayList.size==0){
                            binding.tvAlert.visibility=View.VISIBLE
                        }else {
                            binding.tvAlert.visibility=View.GONE
                        }
                        adapter.setData(arrayList)
                        binding.shimmer.stopShimmer()
                        binding.shimmer.visibility = View.GONE
                        binding.swipeRefresh.isRefreshing=false
                        binding.recyclerview.visibility=View.VISIBLE
                    }
                }
                is Resource.Failure -> {
                    context?.toast(it.exception?.message!!)
                    binding.shimmer.stopShimmer()
                    binding.shimmer.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing=false
                    binding.recyclerview.visibility=View.VISIBLE
                }
                is Resource.Loading -> {
                    binding.shimmer.startShimmer()
                    binding.shimmer.visibility = View.VISIBLE
                    binding.recyclerview.visibility=View.GONE
                }
            }
        })
    }
}