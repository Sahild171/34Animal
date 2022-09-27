package com.application.animalAlertApp.View.Friends

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.application.animalAlertApp.Adapters.ConnectionsAdapter
import com.application.animalAlertApp.Adapters.SearchUserAdapter
import com.application.animalAlertApp.Interfaces.RemoveFriend
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.Utils.*
import com.application.animalAlertApp.data.Response.Profile.SearchUser
import com.application.animalAlertApp.data.Response.Request.GetRequest
import com.application.animalAlertApp.databinding.ActivityConnectionsBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ConnectionsActivity : AppCompatActivity(), RemoveFriend {
    private val viewModel: FriendsViewModel by viewModels()
    private lateinit var adapter: ConnectionsAdapter
    private lateinit var binding: ActivityConnectionsBinding
    private lateinit var list: ArrayList<GetRequest>
    private lateinit var searchUserAdapter: SearchUserAdapter
    private lateinit var searchlist: ArrayList<SearchUser>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_connections)
        binding = ActivityConnectionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        list = ArrayList()
        searchlist = ArrayList()
        initlize()
        if (checkForInternet(this)) {
            getdatafriends()
        } else {
            toast("No Internet connection")
        }


        binding.imgBack.setOnClickListener {
            finish()
        }


        searchUserAdapter = SearchUserAdapter(this, searchlist)
        binding.searchRecyclerview.adapter = searchUserAdapter

        binding.imgSearch.setOnClickListener {
            binding.searchLayout.visibility = View.VISIBLE
            binding.imgSearch.visibility = View.GONE

            binding.etSearch.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
        }

        searchObserver()
        binding.imgClose.setOnClickListener {
            searchlist.clear()
            binding.etSearch.setText("")
            hideSoftKeyboard(this)
            binding.searchLayout.visibility = View.GONE
            binding.imgSearch.visibility = View.VISIBLE
        }
        binding.etSearch.addTextChangedListener(textwatcher)
    }

    private fun searchObserver() {
        viewModel.searchuser.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        searchlist.clear()
                        searchlist.addAll(it.value.searchUser)
                        searchUserAdapter.setData(searchlist)
                    } else {
                        searchlist.clear()
                        searchUserAdapter.setData(searchlist)
                    }
                }
                is Resource.Loading -> {

                }
                is Resource.Failure -> {
                    toast("" + it.exception!!.message)
                }
            }
        })
    }

    private val textwatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            if (checkForInternet(this@ConnectionsActivity)) {
                viewModel.searchuser(p0.toString())
            } else {
                toast("No Internet Connection")
            }
        }

    }

    private fun getdatafriends() {
        viewModel.getfirends("Accepted")
        viewModel.frienddata.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    list.clear()
                    binding.shimmer.stopShimmer()
                    binding.shimmer.visibility = View.GONE
                    if (it.value.status == 200) {
                        //   Log.e("reponse",""+it.value.toString())
                        list.addAll(it.value.getRequests)
                        if (list.size == 0) {
                            binding.tvNorequest.visibility = View.VISIBLE
                        } else {
                            binding.tvNorequest.visibility = View.GONE
                        }
                        adapter.setData(list)
                    }
                }
                is Resource.Failure -> {
                    binding.shimmer.stopShimmer()
                    binding.shimmer.visibility = View.GONE
                }
                is Resource.Loading -> {
                    binding.shimmer.startShimmer()
                }
            }
        })
    }

    private fun initlize() {
        adapter = ConnectionsAdapter(this, list, this)
        binding.recyclerview.adapter = adapter
    }


    override fun onRemove(pos: Int, id: String) {
        if (checkForInternet(this)) {
            removemyFriend(pos, id)
        } else {
            toast("No Internet connection")
        }
    }

    private fun removemyFriend(positon: Int, idd: String) {
        val pd: Dialog = MyProgressBar.progress(this@ConnectionsActivity)
        viewModel.removefriends(idd)
        viewModel.removefrienddata.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    pd.dismiss()
                    if (it.value.status == 200) {
                        toast("" + it.value.message)
                        list.removeAt(positon)
                        adapter.setData(list)
                        if (list.size == 0) {
                            binding.tvNorequest.visibility = View.VISIBLE
                        } else {
                            binding.tvNorequest.visibility = View.GONE
                        }
                    }
                }
                is Resource.Failure -> {
                    toast("" + it.exception!!.message)
                    pd.dismiss()
                }
                is Resource.Loading -> {
                    pd.show()
                }
                is Resource.Empty -> {
                    pd.show()
                }
            }
        })
    }
}