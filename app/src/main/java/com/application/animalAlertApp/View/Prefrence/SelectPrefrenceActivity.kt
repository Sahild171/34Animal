package com.application.animalAlertApp.View.Prefrence

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.SharedPrefrences.MySharedPreferences
import com.application.animalAlertApp.SharedPrefrences.UserPreferences
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.View.Pet.AddPetActivity
import com.application.animalAlertApp.databinding.ActivitySelectPrefrenceBinding
import com.kaopiz.kprogresshud.KProgressHUD
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectPrefrenceActivity : AppCompatActivity() {
    private var prefrence: String? = null
    private val viewModel: PrefrenceViewModel by viewModels()
    private lateinit var binding: ActivitySelectPrefrenceBinding
    private lateinit var progress: KProgressHUD
    private var checklover: Boolean = false
    private var checkowner: Boolean = false
    private var checkvolunteer: Boolean = false
    private var prefrencelist: ArrayList<String>? = null
    private var Screentype: String = ""
    private lateinit var userPreferences: UserPreferences
    private var Prefrences = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_select_prefrence)
        binding = ActivitySelectPrefrenceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefrencelist = ArrayList()
        userPreferences = UserPreferences(this)
        Screentype = intent.getStringExtra("Screentype").toString()
        if (Screentype.equals("Change")) {
            binding.textView4.setText("Change Preferences")
            userPreferences.getprefrence.asLiveData().observe(this, androidx.lifecycle.Observer {
                if (it != null) {
                    Prefrences = it
                    if (Prefrences.contains("petlover")) {
                        checklover = false
                        selectpetlover()
                    }
                    if (Prefrences.contains("petowner")) {
                        checkowner = false
                        selectpetowner()
                    }
                    if (Prefrences.contains("volunteer")) {
                        checkvolunteer = false
                        selectvolunteer()
                    }
                }
            })
        }

        progress = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please Wait")

        selectprefrence()
        changeprefrence()
    }

    private fun changeprefrence() {
        viewModel.changeprefrence.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                      //  toast("" + it.value.message)
                        GlobalScope.launch(Dispatchers.Main) {
                            userPreferences.saveprefrence(prefrence.toString())
                        }
                        progress.dismiss()
                        finish()
                    } else {
                        toast("" + it.value.message)
                        progress.dismiss()
                    }
                }
                is Resource.Failure -> {
                    toast("" + it.exception!!.message)
                    progress.dismiss()
                }
                is Resource.Loading -> {
                    progress.show()
                }
            }
        })
    }

    private fun saveprefrence() {
        binding.apply {
            lifecycleScope.launchWhenStarted {
                val mySharedPreferences = MySharedPreferences(this@SelectPrefrenceActivity)
                //  Log.e("params", mySharedPreferences.getUserid()!! + "=" + prefrence!!)
                viewModel.saveprefrence(
                    mySharedPreferences.getUserid()!!,
                    prefrence!!
                ).catch { e ->
                    toast("" + e.message)
                    progress.dismiss()
                }.onStart {
                    progress.show()
                }.collect { response ->
                    progress.dismiss()
                    if (response.status == 200) {
                        progress.dismiss()
                        userPreferences.saveprefrence(prefrence.toString())
                        val intent =
                            Intent(this@SelectPrefrenceActivity, AddPetActivity::class.java)
                        intent.putExtra("Screentype", "Prefrences")
                        startActivity(intent)
                        finish()
                    } else {
                        toast("" + response.message)
                        progress.dismiss()
                    }
                }
            }
        }
    }

    private fun selectprefrence() {
        binding.apply {
            imgPetlover.setOnClickListener {
                selectpetlover()
            }
            imgPetowner.setOnClickListener {
                selectpetowner()
            }
            imgVolunteer.setOnClickListener {
                selectvolunteer()
            }
            btContinue.setOnClickListener {
                if (prefrencelist?.size == 0 && prefrencelist == null) {
                    toast("Please Select Preference")
                } else if (prefrencelist?.size!! > 0) {
                    prefrence = prefrencelist.toString().replace("[", "").replace("]", "")
                    if (Screentype.equals("Change")) {
                        viewModel.changePrfrences(prefrence!!)
                    } else {
                        saveprefrence()
                    }
                } else {
                    toast(" Select Preference")
                }
            }
        }
    }


    private fun selectpetlover() {
        if (!checklover) {
            checklover = true
            binding.imgLover.visibility = View.VISIBLE
            prefrencelist?.add("petlover")
        } else {
            checklover = false
            binding.imgLover.visibility = View.INVISIBLE
            prefrencelist?.remove("petlover")
        }
    }

    private fun selectpetowner() {
        if (!checkowner) {
            checkowner = true
            binding.imgOwner.visibility = View.VISIBLE
            prefrencelist?.add("petowner")
        } else {
            checkowner = false
            binding.imgOwner.visibility = View.INVISIBLE
            prefrencelist?.remove("petowner")
        }
    }

    private fun selectvolunteer() {
        if (!checkvolunteer) {
            checkvolunteer = true
            binding.volunteer.visibility = View.VISIBLE
            prefrencelist?.add("volunteer")
        } else {
            checkvolunteer = false
            binding.volunteer.visibility = View.INVISIBLE
            prefrencelist?.remove("volunteer")
        }
    }

}