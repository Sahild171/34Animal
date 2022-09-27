package com.application.animalAlertApp.View.Requsts

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.application.animalAlertApp.Adapters.RequestAdapter
import com.application.animalAlertApp.Interfaces.RequestInterface
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.Utils.checkForInternet
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.data.Response.Request.GetRequest
import com.application.animalAlertApp.databinding.ActivityReqeustBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ReqeustActivity : AppCompatActivity(), RequestInterface {
    private lateinit var adapter: RequestAdapter
    private lateinit var binding: ActivityReqeustBinding
    private val viewmodel: GetRequestsViewModel by viewModels()
    private lateinit var list: ArrayList<GetRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_reqeust)
        binding = ActivityReqeustBinding.inflate(layoutInflater)
        setContentView(binding.root)
        list = ArrayList()

        adapter = RequestAdapter(this, this, list)
        binding.recyclerview.adapter = adapter
        if (checkForInternet(this)) {
            getrequestdata()
        } else {
            toast("No Internet connection")
        }
        binding.imgBack.setOnClickListener { finish() }
    }

    private fun getrequestdata() {
        viewmodel.getrequests("Pending")
        viewmodel.userresuestdata1.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    list.clear()
                    binding.shimmer.stopShimmer()
                    binding.shimmer.visibility = View.GONE
                    if (it.value.status == 200) {
                        list.addAll(it.value.getRequests)
                        if (list.size == 0) {
                            binding.tvNorequest.visibility = View.VISIBLE
                        } else {
                            binding.tvNorequest.visibility = View.GONE
                        }
                        adapter.setData(list)
                    } else {
                        toast("" + it.value.message)
                    }
                }
                is Resource.Failure -> {
                    toast("" + it.exception!!.message)
                    binding.shimmer.stopShimmer()
                    binding.shimmer.visibility = View.GONE
                }
                is Resource.Loading -> {
                    binding.shimmer.startShimmer()
                }
            }
        })
    }


    override fun onAccept(pos: Int, id: String, senderId: String) {
        if (checkForInternet(this)) {
            DoAcceptOrReject("Accepted", id, pos, senderId)
        } else {
            toast("No Internet connection")
        }
    }

    override fun onDecline(pos: Int, id: String) {
        if (checkForInternet(this)) {
            DoAcceptOrReject("Rejected", id, pos, "")
        } else {
            toast("No Internet connection")
        }
    }

    private fun DoAcceptOrReject(status: String, id: String, position: Int, senderId: String) {
        val pd: Dialog = MyProgressBar.progress(this@ReqeustActivity)
        viewmodel.acceptorreject(status, id, senderId)
        viewmodel.acceptreject.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    pd.dismiss()
                    if (it.value.status == 200) {
                        list.removeAt(position)
                        adapter.setData(list)
                        if (list.size == 0) {
                            binding.tvNorequest.visibility = View.VISIBLE
                        } else {
                            binding.tvNorequest.visibility = View.GONE
                        }
                    }else{
                          toast("" + it.value.message)
                    }
                }
                is Resource.Failure -> {
                    pd.dismiss()
                    toast("" + it.exception!!.message)
                }
                is Resource.Loading -> {
                    pd.show()
                }
                is Resource.Empty -> {
                    pd.dismiss()
                }
            }
        })
    }

}