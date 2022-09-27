package com.application.animalAlertApp.View.Profile

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.application.animalAlertApp.Adapters.AllPetsAdapter
import com.application.animalAlertApp.Interfaces.PetsUpdateInterface
import com.application.animalAlertApp.Interfaces.SeePetInterface
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.Utils.MyDialog
import com.application.animalAlertApp.Utils.checkForInternet
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.View.AddAlerts.MyPetsState
import com.application.animalAlertApp.View.Pet.AddPetActivity
import com.application.animalAlertApp.data.Response.MessageXX
import com.application.animalAlertApp.databinding.ActivitySeeAllPetsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class SeeAllPetsActivity : AppCompatActivity(), SeePetInterface {
    private lateinit var binding: ActivitySeeAllPetsBinding
    private lateinit var adpter: AllPetsAdapter
    private val viewModel: AllPetViewModel by viewModels()
    private lateinit var pet_list: ArrayList<MessageXX>
    private lateinit var pd: Dialog
    private var selectedpos: Int = 0
    private lateinit var selectedlist: ArrayList<MessageXX>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_see_all_pets)
        binding = ActivitySeeAllPetsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pet_list = ArrayList()
        selectedlist = ArrayList()
        pd = MyProgressBar.progress(this)

        getmypets()
        deletepetobserver()

        binding.imgBack.setOnClickListener {
            finish()
        }

        val intent = IntentFilter()
        intent.addAction("RefreshPets")
        registerReceiver(reciver, intent)
    }

    val reciver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (checkForInternet(this@SeeAllPetsActivity)) {
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

    private fun getmypets() {
        if (checkForInternet(this)) {
            viewModel.getpets()
        } else {
            toast("No Internet connection")
        }
        viewModel.mypetData.observe(this, Observer {
            binding.apply {
                when (it) {
                    is MyPetsState.Success -> {
                        if (it.data.status == 200) {
                            pd.dismiss()
                            pet_list.clear()
                            pet_list.addAll(it.data.findPet)
                            adpter = AllPetsAdapter(
                                this@SeeAllPetsActivity,
                                pet_list,
                                this@SeeAllPetsActivity
                            )
                            binding.recyclerview.adapter = adpter
                        }else{
                            pd.dismiss()
                            toast(""+it.data.message)
                        }
                    }
                    is MyPetsState.Failure -> {
                        toast("" + it.msg)
                        pd.dismiss()
                        Log.e("mypet", "" + it.msg)
                    }
                    is MyPetsState.Loading -> {
                        pd.show()
                        //    progress.show()
                    }
                }
            }
        })
    }


    private fun deletepetobserver() {
        viewModel.deletePetdata.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        pet_list.removeAt(selectedpos)
                        if (pet_list.size == 0) {
//                            binding.tvNopet.visibility = View.VISIBLE
                        }
                        adpter = AllPetsAdapter(
                            this@SeeAllPetsActivity, pet_list, this@SeeAllPetsActivity
                        )
                        binding.recyclerview.adapter = adpter

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


    override fun onseepet(pos: Int, data: MessageXX) {
        selectedpos = pos
        selectedlist.clear()
        selectedlist.add(data)
        val dailog = MyDialog()
        dailog.see_pet_detail(this, data, "MyPets", updateInterface)
    }

    val updateInterface = object : PetsUpdateInterface {
        override fun oneditpet() {
            val intent = Intent(this@SeeAllPetsActivity, AddPetActivity::class.java)
            val args = Bundle()
            args.putSerializable("pet_list", `pet_list` as Serializable?)
            intent.putExtra("BUNDLE", args)
            startActivity(intent)
        }

        override fun ondeletepet() {
            if (checkForInternet(this@SeeAllPetsActivity)) {
                viewModel.deletepet(selectedlist[0]._id)
            } else {
                toast("No Internet Connection")
            }
        }
    }
}