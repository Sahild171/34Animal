package com.application.animalAlertApp.View.Payments


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.Utils.checkForInternet
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.databinding.ActivityCardDetailBinding
import com.braintreepayments.cardform.utils.CardType
import com.braintreepayments.cardform.view.CardForm
import com.kaopiz.kprogresshud.KProgressHUD
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCardDetailBinding
    private val viewmodel: PaymentViewModel by viewModels()
    private lateinit var progress: KProgressHUD
    private var plan=""
    private val SUPPORTED_CARD_TYPES = arrayOf(
        CardType.VISA, CardType.MASTERCARD, CardType.DISCOVER,
        CardType.AMEX, CardType.DINERS_CLUB, CardType.JCB, CardType.MAESTRO, CardType.UNIONPAY,
        CardType.HIPER, CardType.HIPERCARD
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        plan=intent.getStringExtra("plan").toString()


        progress = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Payment Processing")

        binding.imgBack.setOnClickListener {
            finish()
        }

        paymentdata()
        paymentdata6()
        binding.btPay.setOnClickListener {
            if (binding.cardform.isValid) {
                if (checkForInternet(this)) {
                    if (plan.equals("Monthly")){
                        viewmodel.makepayment(
                            binding.cardform.cardNumber,
                            binding.cardform.expirationMonth,
                            binding.cardform.expirationYear,
                            binding.cardform.cvv
                        )
                    }else {
                        viewmodel.makepayment6month(
                            binding.cardform.cardNumber,
                            binding.cardform.expirationMonth,
                            binding.cardform.expirationYear,
                            binding.cardform.cvv
                        )
                    }

                } else {
                    toast("No Internet Connection")
                }
            } else {
                toast("Incorrect details")
            }
        }

        binding.supportedCardTypes.setSupportedCardTypes(*SUPPORTED_CARD_TYPES)
        binding.cardform.cardRequired(true)
            .expirationRequired(true)
            .cvvRequired(true)
            .postalCodeRequired(true)
            .cardholderName(CardForm.FIELD_REQUIRED)
            .setup(this)

        val param =
            binding.cardform.expirationDateEditText.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(0, 0, 10, 0)
        binding.cardform.expirationDateEditText.layoutParams = param

        binding.cardform.getCvvEditText()
            .setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD)
    }

    private fun paymentdata6() {
        viewmodel.paymentdata6mon.observe(this@CardDetailActivity, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        progress.dismiss()
                        toast("" + it.value.message)
                        val intent = Intent()
                        intent.setAction("ChangePaymentStatus")
                        sendBroadcast(intent)
                        finish()
                    } else {
                        progress.dismiss()
                        toast("" + it.value.message)
                    }
                }
                is Resource.Failure -> {
                    progress.dismiss()
                    toast("" + it.exception!!.message)
                }
                is Resource.Loading -> {
                    progress.show()
                }
            }
        })

    }

    private fun paymentdata() {
        viewmodel.paymentdata.observe(this@CardDetailActivity, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        progress.dismiss()
                        toast("" + it.value.message)
                        val intent = Intent()
                        intent.setAction("ChangePaymentStatus")
                        sendBroadcast(intent)
                        finish()
                    } else {
                        progress.dismiss()
                        toast("" + it.value.message)
                    }
                }
                is Resource.Failure -> {
                    progress.dismiss()
                    toast("" + it.exception!!.message)
                }
                is Resource.Loading -> {
                    progress.show()
                }
            }
        })
    }


}