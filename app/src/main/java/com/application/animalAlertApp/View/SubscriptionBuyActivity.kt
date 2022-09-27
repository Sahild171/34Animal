package com.application.animalAlertApp.View


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.application.animalAlertApp.SharedPrefrences.MySharedPreferences
import com.application.animalAlertApp.View.Payments.CardDetailActivity
import com.application.animalAlertApp.View.Shops.CreateShopActivity
import com.application.animalAlertApp.databinding.ActivitySubscriptionBuyBinding


class SubscriptionBuyActivity : AppCompatActivity() {
    private lateinit var mySharedPreferences: MySharedPreferences
    private lateinit var binding: ActivitySubscriptionBuyBinding
    private var Screenstatus: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_subscription_buy)
        binding = ActivitySubscriptionBuyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mySharedPreferences = MySharedPreferences(this)
        Screenstatus = intent.getStringExtra("Screentype").toString()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding.btBuynow.setOnClickListener {
            if (Screenstatus.equals("Payment")) {
                val intent = Intent(this, CardDetailActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, CreateShopActivity::class.java)
                startActivity(intent)
                finish()
            }

        }
    }


}