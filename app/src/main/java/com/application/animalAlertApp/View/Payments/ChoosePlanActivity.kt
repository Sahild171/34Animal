package com.application.animalAlertApp.View.Payments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.application.animalAlertApp.R
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.databinding.ActivityChoosePlanBinding

class ChoosePlanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChoosePlanBinding
    private var plan = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChoosePlanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // setContentView(R.layout.activity_choose_plan)

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.cardMonthly.setOnClickListener {
            selectMonthly()
        }

        binding.cardYearly.setOnClickListener {
            selectAnnualy()
        }


        binding.startSubs.setOnClickListener {
            if (!plan.equals("")) {
                val intent = Intent(this, CardDetailActivity::class.java)
                intent.putExtra("plan", plan)
                startActivity(intent)
                finish()
            } else {
                toast("Please Select Subscription Plan")
            }
        }

    }

    private fun selectMonthly() {
        binding.cardMonthly.strokeWidth = 4
        binding.cardMonthly.strokeColor = resources.getColor(R.color.green)

        binding.cardYearly.strokeWidth = 0
        plan = "Monthly"
    }

    private fun selectAnnualy() {
        binding.cardYearly.strokeWidth = 4
        binding.cardYearly.strokeColor = resources.getColor(R.color.green)
        binding.cardMonthly.strokeWidth = 0
        plan = "Yearly"
    }

}