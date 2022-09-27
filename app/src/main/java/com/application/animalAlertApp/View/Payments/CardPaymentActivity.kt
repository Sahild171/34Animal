package com.application.animalAlertApp.View.Payments


import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.R
import com.application.animalAlertApp.Utils.checkForInternet
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.databinding.ActivityCardPaymentBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale


@AndroidEntryPoint
class CardPaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCardPaymentBinding
    private lateinit var pd: Dialog
    private val viewmodel: PaymentViewModel by viewModels()
    private var subscriptionStatus = ""
    private var plan = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardPaymentBinding.inflate(layoutInflater)
        pd = MyProgressBar.progress(this)
        setContentView(binding.root)

        binding.imgBack.setOnClickListener {
            finish()
        }

        if (checkForInternet(this)) {
            viewmodel.getsubscriptionstatus()
        } else {
            toast("No Internet Connection")
        }


        binding.cancelSubs.setOnClickListener {
            if (checkForInternet(this)) {
                if (subscriptionStatus.equals("Expired")) {
                    if (!plan.equals("")) {
                        val intent = Intent(this, CardDetailActivity::class.java)
                        intent.putExtra("plan", plan)
                        startActivity(intent)
                        finish()
                    } else {
                        toast("Please Select Subscription Plan")
                    }
//                    val intent = Intent(this, SubscriptionBuyActivity::class.java)
//                    intent.putExtra("Screentype", "Payment")
//                    startActivity(intent)
                } else {
                    viewmodel.cancelsubs()
                }


            } else {
                toast("No Internet Connection")
            }
        }
        cancelsubsobserver()
        getsubscriptionObserver()


        binding.cardMonthly.setOnClickListener {
            selectMonthly()
        }

        binding.cardYearly.setOnClickListener {
            selectAnnualy()
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

    private fun getsubscriptionObserver() {
        viewmodel.subscriptionstatus.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        //  getDateTime(it.value.dates.end_date)
                        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        val calendar1 = Calendar.getInstance()
                        val calendar2 = Calendar.getInstance()

                        val date1 = dateFormat.parse(it.value.currentDate)
                        val date2 = dateFormat.parse(it.value.dates.end_date)
                        calendar1.time = date1
                        calendar2.time = date2

                        if (it.value.dates.paymentType.equals("Bi-annualy")){
                            binding.cardMonthly.visibility= View.GONE
                            binding.cardYearly.visibility= View.VISIBLE
                        }else {
                            binding.cardYearly.visibility= View.GONE
                            binding.cardMonthly.visibility= View.VISIBLE
                        }

                        ///Comparing Dates

                        if (calendar1.compareTo(calendar2).equals(0)) {
                            subscriptionStatus = "Equal"
                            binding.cancelSubs.setText("Cancel Subscription")
                        } else if (calendar1.compareTo(calendar2).equals(-1)) {
                            subscriptionStatus = "NotExpired"
                            binding.tvActive.setText("Active")
                            binding.tvActive.setTextColor(resources.getColor(R.color.green))
                             binding.tvInfo.setText("Subscription Will be end on  "+it.value.dates.end_date)
                            binding.tvInfoYearly.setText("Subscription Will be end on  "+it.value.dates.end_date)
                            binding.cancelSubs.setText("Cancel Subscription")
                        } else if (calendar1.compareTo(calendar2).equals(1)) {
                            subscriptionStatus = "Expired"
                            binding.tvActive.setText("Inactive")
                            binding.tvActiveYearly.setText("Inactive")
                            binding.tvActiveYearly.setTextColor(resources.getColor(R.color.red))
                            binding.tvActive.setTextColor(resources.getColor(R.color.red))
                            binding.tvInfo.setText("Your Subscription is Expired")
                            binding.tvInfoYearly.setText("Your Subscription is Expired")
                            binding.cancelSubs.setText("Start Subscription")
                        } else {
                            binding.cancelSubs.setText("Start Subscription")
                        }
                        pd.dismiss()
                        if (it.value.Plan.equals("Cancel")) {
                            binding.cancelSubs.setText("Cancelled Subscription")
                        }
                    } else {
                        pd.dismiss()
                        subscriptionStatus = "Expired"
                        binding.tvActive.setText("Inactive")
                        binding.tvActiveYearly.setText("Inactive")
                        binding.tvActiveYearly.setTextColor(resources.getColor(R.color.red))
                        binding.tvActive.setTextColor(resources.getColor(R.color.red))
                        binding.tvInfoYearly.setText("You dont have any Subscription yet.")
                        binding.tvInfo.setText("You dont have any Subscription yet.")
                        binding.cancelSubs.setText("Start Subscription")
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

    private fun cancelsubsobserver() {
        viewmodel.cancelsubs.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        binding.cancelSubs.setText("Cancelled Subscription")
//                        binding.tvInfo.setText("Your Plan will be expired soon")
                        pd.dismiss()
                    } else {
                        pd.dismiss()
                        toast("" + it.value.message)
//                        val intent = Intent(this, SubscriptionBuyActivity::class.java)
//                        intent.putExtra("Screentype", "Payment")
//                        startActivity(intent)
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

}