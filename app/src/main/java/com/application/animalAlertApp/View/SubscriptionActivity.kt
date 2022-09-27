package com.application.animalAlertApp.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.application.animalAlertApp.R
import com.application.animalAlertApp.View.Payments.CardDetailActivity

class SubscriptionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)
    }

    fun CloseSubscription(view: android.view.View) {
        finish()
    }


    fun ContinueClick(view: android.view.View) {
        val intent=Intent(this, CardDetailActivity::class.java)
        startActivity(intent)
    }
}