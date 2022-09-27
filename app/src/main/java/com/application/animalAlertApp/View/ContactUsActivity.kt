package com.application.animalAlertApp.View

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.Utils.checkForInternet
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.View.ContactUs.ConatctViewModel
import com.application.animalAlertApp.databinding.ActivityContactUsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactUsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContactUsBinding
    private val viewmodel: ConatctViewModel by viewModels()
    private lateinit var pd: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactUsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pd = MyProgressBar.progress(this)
        // setContentView(R.layout.activity_contact_us)


        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.btSend.setOnClickListener {
            if (validation()) {
                if (checkForInternet(this)) {
                    viewmodel.contactus(
                        binding.editTextTextPersonName.text.toString().trim(),
                        binding.editTextTextPersonemail.text.toString().trim(),
                        binding.editTextTextPersonmessage.text.toString().trim()
                    )
                } else {
                    toast("No Internet connection")
                }
            }
        }
        getdata()

    }

    private fun getdata() {
        viewmodel.contactdata.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        finish()
                        pd.dismiss()
                    } else {
                        pd.dismiss()
                        toast("" + it.value.message)
                    }
                }
                is Resource.Failure -> {
                    toast("Exception:" + it.exception!!.message)
                    pd.dismiss()
                }
                is Resource.Loading -> {
                    pd.show()
                }
            }
        })
    }


    private fun validation(): Boolean {
        if (binding.editTextTextPersonName.text.toString().trim().isEmpty()) {
            binding.editTextTextPersonName.error = "Empty"
            return false
        } else if (binding.editTextTextPersonemail.text.toString().trim().isEmpty()) {
            binding.editTextTextPersonemail.error = "Empty"
            return false
        } else if (binding.editTextTextPersonmessage.text.toString().trim().isEmpty()) {
            binding.editTextTextPersonmessage.error = "Empty"
            return false
        }
        return true
    }


}