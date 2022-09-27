package com.application.animalAlertApp.View.Settings


import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.SharedPrefrences.MySharedPreferences
import com.application.animalAlertApp.Utils.checkForInternet
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.View.Prefrence.SelectPrefrenceActivity
import com.application.animalAlertApp.databinding.ActivitySettingsBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val viewmodel: Settingviewmodel by viewModels()
    private lateinit var pd: Dialog
    private lateinit var shared: MySharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_settings)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        shared = MySharedPreferences(this)
        pd = MyProgressBar.progress(this@SettingsActivity)


        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.btSave.setOnClickListener {
            if (validation()) {
                if (checkForInternet(this)) {
                    dochangepasword()
                } else {
                    toast("No internet connection")
                }
            }
        }

        if (checkForInternet(this)) {
            viewmodel.getuserprofile(shared.getUserid()!!)
        } else {
            toast("No Internet Connection")
        }


        if (shared.getnotificationStatus().equals(0)) {
            binding.switchNoti.isChecked = false
        } else {
            binding.switchNoti.isChecked = true
        }



        binding.switchNoti.setOnCheckedChangeListener(object :
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if (p1) {
                    toast("Your notification is on")
                    shared.setnotificationStatus(1)

                } else {
                    shared.setnotificationStatus(0)
                    toast("Your notification is off")
                }
            }
        })

        binding.switchLocation.setOnCheckedChangeListener(object :
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if (p1) {
                    viewmodel.editlocationvisibility(true)
                } else {
                    viewmodel.editlocationvisibility(false)
                }
            }
        })
        binding.tabChangePrefrences.setOnClickListener {
            val intent = Intent(this, SelectPrefrenceActivity::class.java)
            intent.putExtra("Screentype", "Change")
            startActivity(intent)
        }

        viewmodel.editlocationvisibility.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        toast("" + it.value.message)
                    } else {
                        toast("" + it.value.message)
                    }
                }
                is Resource.Failure -> {
                    toast("" + it.exception!!.message)
                }
                is Resource.Loading -> {

                }

            }
        })


        viewmodel.userprofiledata.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                      //  toast(""+it.value.message)
                        if (it.value.userProfile[0].locationVisibility) {
                            binding.switchLocation.isChecked = true
                        } else {
                            binding.switchLocation.isChecked = false
                        }
                    } else {
                        toast("" + it.value.message)
                    }
                }
                is Resource.Failure -> {
                    toast("" + it.exception!!.message)
                }
                is Resource.Loading -> {

                }

            }
        })
    }

    private fun dochangepasword() {
        viewmodel.changepassword(
            binding.etOldpassword.text.toString().trim(),
            binding.etNewpassword.text.toString().trim()
        )
        viewmodel.changepassworddata.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        pd.dismiss()
                        binding.etOldpassword.setText("")
                        binding.etConpassword.setText("")
                        binding.etNewpassword.setText("")
                        toast("" + it.value.message)
                    } else {
                        pd.dismiss()
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
            }
        })
    }

    private fun validation(): Boolean {
        val pass = binding.etNewpassword.text.toString().trim()
        if (binding.etOldpassword.text.toString().trim().isEmpty()) {
            binding.etOldpassword.error = "Empty"
            return false
        } else if (binding.etNewpassword.text.toString().trim().isEmpty()) {
            binding.etNewpassword.error = "Empty"
            return false
        } else if (!pass.equals(binding.etConpassword.text.toString().trim())) {
            toast("Password & Confirm password not match")
            return false
        }
        return true
    }

}




