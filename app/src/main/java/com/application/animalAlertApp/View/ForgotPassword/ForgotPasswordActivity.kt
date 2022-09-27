package com.application.animalAlertApp.View.ForgotPassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.application.animalAlertApp.databinding.ActivityForgotPasswordBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding:ActivityForgotPasswordBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


}