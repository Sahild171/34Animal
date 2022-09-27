package com.application.animalAlertApp.View.Profile

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.animalAlertApp.Adapters.AddedPetAdapter
import com.application.animalAlertApp.Adapters.ProfilePostAdapter
import com.application.animalAlertApp.Interfaces.PetsUpdateInterface
import com.application.animalAlertApp.Interfaces.SeePetInterface
import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.R
import com.application.animalAlertApp.SharedPrefrences.UserPreferences
import com.application.animalAlertApp.Utils.MyDialog
import com.application.animalAlertApp.Utils.checkForInternet
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.View.AddAlerts.MyPetsState
import com.application.animalAlertApp.View.BusinessProfile.BusinessProfileActivity
import com.application.animalAlertApp.View.Friends.ConnectionsActivity
import com.application.animalAlertApp.View.MyProfilePostActivity
import com.application.animalAlertApp.View.Pet.AddPetActivity
import com.application.animalAlertApp.View.Post.MyPostState
import com.application.animalAlertApp.View.Requsts.ReqeustActivity
import com.application.animalAlertApp.data.Response.MessageXX
import com.application.animalAlertApp.data.Response.Post.GetPost
import com.application.animalAlertApp.databinding.ActivityMyProfileBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.coroutines.*
import java.io.Serializable


@AndroidEntryPoint
class MyProfileActivity : AppCompatActivity(), SeePetInterface {
    private lateinit var adapter: AddedPetAdapter
    private lateinit var adapter1: ProfilePostAdapter
    private lateinit var binding: ActivityMyProfileBinding
    private lateinit var userPreferences: UserPreferences
    private val viewModel: MyProfileViewModel by viewModels()
    private lateinit var pet_list: ArrayList<MessageXX>
    private lateinit var post_list: ArrayList<GetPost>
    private var see_size: Int? = null
    private var see_post: Int? = null
    private lateinit var selectedlist: ArrayList<MessageXX>
    private var selectedpos: Int = 0
    private lateinit var pd: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //    setContentView(R.layout.activity_my_profile)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pd = MyProgressBar.progress(this)

        pet_list = ArrayList()
        post_list = ArrayList()
        selectedlist = ArrayList()

        intialize()
        //  setdata()
        if (checkForInternet(this)) {
            getmypets()
            getmyposts()
        } else {
            toast("No Internet connection")
        }

        tablayout()


        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.imgEdit.setOnClickListener {
            val intent = Intent(this@MyProfileActivity, EditProfileActivity::class.java)
            startActivity(intent)
        }
        binding.tvSeeall.setOnClickListener {
            val intent = Intent(this@MyProfileActivity, SeeAllPetsActivity::class.java)
            startActivity(intent)
        }
        binding.tvSeeallpost.setOnClickListener {
            val intent = Intent(this@MyProfileActivity, MyProfilePostActivity::class.java)
            startActivity(intent)
        }

        getshopdata()

        binding.button12.setOnClickListener {
            if (checkForInternet(this)) {
                viewModel.checkshopdata()
            } else {
                toast("No internet connection")
            }
        }


        deletepetobserver()

        val intent = IntentFilter()
        intent.addAction("RefreshPets")
        registerReceiver(reciver, intent)

    }

    val reciver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (checkForInternet(this@MyProfileActivity)) {
                viewModel.getpets()
            } else {
                toast("No Internet Connection")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(reciver)
    }


    private fun tablayout() {
        binding.tabLayout.setOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
//                    toast("0")
                } else if (tab.position == 1) {
                    val intent = Intent(this@MyProfileActivity, ConnectionsActivity::class.java)
                    startActivity(intent)
                } else if (tab.position == 2) {
                    val intent = Intent(this@MyProfileActivity, ReqeustActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // called when tab unselected
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // called when a tab is reselected
            }
        })
    }

    private fun setdata() {
        userPreferences = UserPreferences(applicationContext)

        userPreferences.getname.asLiveData().observe(this, androidx.lifecycle.Observer {
            binding.tvName.setText(it)
        })
        userPreferences.getphone.asLiveData().observe(this, androidx.lifecycle.Observer {
            binding.tvNo.setText(it)
        })
        userPreferences.getlocation.asLiveData().observe(this, androidx.lifecycle.Observer {
            binding.tvLocation.setText(it)
        })

        userPreferences.getimage.asLiveData().observe(this, androidx.lifecycle.Observer {
            Glide.with(this@MyProfileActivity).load(ApiService.IMAGE_BASE_URL + it)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.placeholder)
                .into(binding.imgProfile)
        })

        userPreferences.getprefrence.asLiveData().observe(this, androidx.lifecycle.Observer { it ->
            val data = it.toString()
            if (data.contains("petowner") && data.contains("petlover") && data.contains("volunteer")) {
                binding.circleProgressview.setBackgroundResource(R.drawable.three_color)
            } else if (data.contains("petowner") && data.contains("petlover")) {
                binding.circleProgressview.setBackgroundResource(R.drawable.two_color)
            } else if (data.contains("petlover") && data.contains("volunteer")) {
                binding.circleProgressview.setBackgroundResource(R.drawable.orangle_purple)
            } else if (data.contains("petowner") && data.contains("volunteer")) {
                binding.circleProgressview.setBackgroundResource(R.drawable.green_purple)
            } else if (data.contains("petowner") && !data.contains("petlover") && !data.contains("volunteer")) {
                binding.circleProgressview.setBackgroundResource(R.drawable.one_color)
            } else if (!data.contains("petowner") && data.contains("petlover") && !data.contains("volunteer")) {
                binding.circleProgressview.setBackgroundResource(R.drawable.orange_color)
            } else if (!data.contains("petowner") && !data.contains("petlover") && data.contains("volunteer"))
                binding.circleProgressview.setBackgroundResource(R.drawable.purple_color)
            else {
                binding.circleProgressview.setBackgroundResource(R.drawable.one_color)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        setdata()
        val tab: TabLayout.Tab = binding.tabLayout.getTabAt(0)!!
        tab.select()
        setdata()
    }


    private fun getmyposts() {
        viewModel.getmyposts()
        viewModel.mypostData.observe(this, Observer {
            binding.apply {
                when (it) {
                    is MyPostState.Success -> {
                        if (it.data.status == 200) {
                            post_list.addAll(it.data.getPost)
                            if (post_list.size > 2) {
                                see_post = 2
                                binding.tvSeeallpost.visibility = View.VISIBLE
                            } else {
                                binding.tvSeeallpost.visibility = View.GONE
                                see_post = post_list.size
                            }
                            if (post_list.size == 0) {
                                binding.tvNopost.visibility = View.VISIBLE
                                binding.tvSeeallpost.visibility = View.GONE
                            } else {
                                binding.tvNopost.visibility = View.GONE
                            }
                            adapter1 = ProfilePostAdapter(
                                this@MyProfileActivity,
                                post_list,
                                see_post!!
                            )
                            binding.recyclerviewPost.adapter = adapter1
                        }
                    }
                    is MyPostState.Failure -> {
                        toast("" + it.msg)
                    }
                    is MyPostState.Loading -> {
                        //    progress.show()
                    }
                    is MyPostState.Empty -> {

                    }
                }
            }
        })
    }

    private fun intialize() {
        val mLayoutManager = LinearLayoutManager(applicationContext)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.recyclerview.layoutManager = mLayoutManager
        binding.recyclerview.itemAnimator = DefaultItemAnimator()
    }

    private fun getmypets() {
        viewModel.getpets()
        viewModel.mypetData.observe(this, Observer {
            binding.apply {
                when (it) {
                    is MyPetsState.Success -> {
                        if (it.data.status == 200) {
                            pet_list.clear()
                            pet_list.addAll(it.data.findPet)
                            if (pet_list.size > 4) {
                                see_size = 4
                                binding.tvSeeall.visibility = View.VISIBLE
                            } else {
                                binding.tvSeeall.visibility = View.GONE
                                see_size = pet_list.size
                            }
                            adapter =
                                AddedPetAdapter(
                                    this@MyProfileActivity,
                                    pet_list,
                                    see_size!!,
                                    this@MyProfileActivity
                                )
                            binding.recyclerview.adapter = adapter
                            if (pet_list.size == 0) {
                                binding.tvNopet.visibility = View.VISIBLE
                            }
                        }
                    }
                    is MyPetsState.Failure -> {
                        toast("" + it.msg)
                        Log.e("mypet", "" + it.msg)
                    }
                    is MyPetsState.Loading -> {
                        //    progress.show()
                    }

                }
            }
        })
    }

    //Interface function
    override fun onseepet(pos: Int, data: MessageXX) {
        selectedlist.clear()
        selectedlist.add(data)
        selectedpos = pos
        val dailog = MyDialog()
        dailog.see_pet_detail(this, data, "MyPets", updateInterface)
    }

    val updateInterface = object : PetsUpdateInterface {
        override fun oneditpet() {
            val intent = Intent(this@MyProfileActivity, AddPetActivity::class.java)
            val args = Bundle()
            args.putSerializable("pet_list", `pet_list` as Serializable?)
            intent.putExtra("BUNDLE", args)
            startActivity(intent)
        }

        override fun ondeletepet() {
            if (checkForInternet(this@MyProfileActivity)) {
                viewModel.deletepet(selectedlist[0]._id)
            } else {
                toast("No Internet Connection")
            }
        }

    }


    private fun deletepetobserver() {
        viewModel.deletePetdata.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        pet_list.removeAt(selectedpos)
                        if (pet_list.size > 4) {
                            see_size = 4
                            binding.tvSeeall.visibility = View.VISIBLE
                        } else {
                            binding.tvSeeall.visibility = View.GONE
                            see_size = pet_list.size
                        }
                        if (pet_list.size == 0) {
                            binding.tvNopet.visibility = View.VISIBLE
                        }
                        adapter =
                            AddedPetAdapter(
                                this@MyProfileActivity,
                                pet_list,
                                see_size!!,
                                this@MyProfileActivity
                            )
                        binding.recyclerview.adapter = adapter

                        pd.dismiss()
                    } else {
                        toast("" + it.value.message)
                        pd.dismiss()
                    }
                }
                is Resource.Failure -> {
                    toast("" + it.exception!!.message)
                    pd.dismiss()
                }
                is Resource.Loading -> {
                    pd.show()
                }
            }
        })
    }


    private fun getshopdata() {
        viewModel.checkshopdata.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        pd.dismiss()
                        if (it.value.shopStatus.equals(1)) {
                            val intent = Intent(this, BusinessProfileActivity::class.java)
                            startActivity(intent)
                        } else {
                            toast("First create Shop")
                        }
                    } else {
                        pd.dismiss()
                        toast("" + it.value.message)
                    }
                }
                is Resource.Failure -> {
                    toast("" + it.exception!!.message)
                    pd.dismiss()
                }
                is Resource.Loading -> {
                    pd.show()
                }
            }
        })
    }


}