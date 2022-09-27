package com.application.animalAlertApp.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import com.application.animalAlertApp.databinding.ActivityTermsandConditionsBinding


class TermsandConditions : AppCompatActivity() {
    private lateinit var binding: ActivityTermsandConditionsBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsandConditionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupwebview()


        binding.btBack.setOnClickListener {
            finish()
        }
    }

    private fun setupwebview() {
        binding.webview.webViewClient = WebViewClient()
        binding.webview.loadUrl("https://animalalertcommunity.org/t&c.html")
        binding.webview.settings.javaScriptEnabled = true
        binding.webview.settings.setSupportZoom(true)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}


