package com.application.animalAlertApp.View.Payments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.databinding.ActivitySavecardBinding
import com.kaopiz.kprogresshud.KProgressHUD
import dagger.hilt.android.AndroidEntryPoint
import android.text.InputType
import android.view.ViewGroup
import com.braintreepayments.cardform.view.CardForm
import com.braintreepayments.cardform.utils.CardType
import com.braintreepayments.cardform.view.CardEditText


@AndroidEntryPoint
class SavecardActivity : AppCompatActivity(), CardEditText.OnCardTypeChangedListener {
    private lateinit var binding: ActivitySavecardBinding
    private val viewModel: PaymentViewModel by viewModels()
    private lateinit var progress: KProgressHUD
    private var cardtype: String = ""
    private var cardId: String = ""
    private val SUPPORTED_CARD_TYPES = arrayOf(
        CardType.VISA, CardType.MASTERCARD, CardType.DISCOVER,
        CardType.AMEX, CardType.DINERS_CLUB, CardType.JCB, CardType.MAESTRO, CardType.UNIONPAY,
        CardType.HIPER, CardType.HIPERCARD
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavecardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cardtype = intent.getStringExtra("SaveCardType").toString()
        cardId = intent.getStringExtra("cardId").toString()


        if (cardtype.equals("Edit")) {
            binding.tvTitle.setText("Edit Card")
            binding.btPay.setText("Edit")
            val cardno = intent.getStringExtra("cardno").toString()
            binding.cardform.cardEditText.setText(cardno)
            val cvc = intent.getStringExtra("cvc").toString()
            binding.cardform.cvvEditText.setText(cvc)
            val month = intent.getStringExtra("month").toString()
            val year = intent.getStringExtra("year").toString()
            binding.cardform.expirationDateEditText.setText(month + year)

            //  binding.etYear.setText(year)
            val cardholdername = intent.getStringExtra("cardholdername").toString()
            binding.cardform.cardholderNameEditText.setText(cardholdername)
        }
        progress = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please Wait")

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

        binding.btPay.setOnClickListener {
            //    if (validations()) {
            //        if (checkForInternet(this)) {
            //              if (cardtype.equals("Add")) {
//                        viewModel.savecard(
//                            binding.etCardnumber.text.toString().trim(),
//                            binding.etMonth.text.toString().trim(),
//                            binding.etYear.text.toString().trim(),
//                            binding.etCvv.text.toString().trim(),
//                            binding.etCardholdername.text.toString().trim())
            //                 } else {
//                        viewModel.updatecard(
//                            cardId,
//                            binding.etCardnumber.text.toString().trim(),
//                            binding.etMonth.text.toString().trim(),
//                            binding.etYear.text.toString().trim(),
//                            binding.etCvv.text.toString().trim(),
//                            binding.etCardholdername.text.toString().trim())
//                    }
//                } else {
//                    toast("No Internet connection")
//                }
//            }
        }
//        getsavedata()
//        getUpdatecarddata()

        binding.imgBack.setOnClickListener {
            finish()
        }
    }

    private fun getUpdatecarddata() {
        viewModel.updatecarddata.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        progress.dismiss()
                        val intent = Intent()
                        intent.putExtra("type", "Edit")
                        intent.putExtra("id", "" + it.value.updateCard._id)
                        intent.putExtra("cardno", "" + it.value.updateCard.number)
                        intent.putExtra("cvc", "" + it.value.updateCard.cvc)
                        intent.putExtra("expmonth", "" + it.value.updateCard.exp_month)
                        intent.putExtra("expyear", "" + it.value.updateCard.exp_year)
                        intent.putExtra("cardholdername", "" + it.value.updateCard.cardHolderName)
                        setResult(0, intent)
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

    override fun onCardTypeChanged(cardType: CardType?) {
        if (cardType == CardType.EMPTY) {
            binding.supportedCardTypes.setSupportedCardTypes(*SUPPORTED_CARD_TYPES);
        } else {
            binding.supportedCardTypes.setSelected(cardType);
        }
    }

//    private fun validations(): Boolean {
//        if (!binding.etCardnumber.text?.length.toString().equals("19")) {
//            binding.etCardnumber.error = "Wrong detail"
//            return false
//        } else if (!binding.etMonth.text.length.toString().equals("2") || binding.etMonth.text.toString().trim().equals("00")) {
//            binding.etMonth.error = "Wrong detail"
//            return false
//        } else if (!binding.etYear.text.length.toString().equals("2") || binding.etYear.text.toString().trim().equals("00")) {
//            binding.etYear.error = "Wrong detail"
//            return false
//        } else if (!binding.etCvv.text.length.toString().equals("3") || binding.etCvv.text.toString().trim().equals("000")) {
//            binding.etCvv.error = "Wrong detail"
//            return false
//        } else if (binding.etCardholdername.text.toString().trim().isEmpty()) {
//            binding.etCardholdername.error = "Empty"
//            return false
//        }
//        return true
//    }

//    private fun getsavedata() {
//        viewModel.savecarddata.observe(this, Observer {
//            when (it) {
//                is Resource.Success -> {
//                    if (it.value.status == 200) {
//                        progress.dismiss()
//                        //  toast("" + it.value.message)
//                        val intent = Intent()
//                        intent.putExtra("type", "Add")
//                        intent.putExtra("id", "" + it.value.docs._id)
//                        intent.putExtra("cardno", "" + it.value.docs.number)
//                        intent.putExtra("cvc", "" + it.value.docs.cvc)
//                        intent.putExtra("expmonth", "" + it.value.docs.exp_month)
//                        intent.putExtra("expyear", "" + it.value.docs.exp_year)
//                        intent.putExtra("cardholdername", "" + it.value.docs.cardHolderName)
//                        setResult(0, intent)
//                        finish()
//                    } else {
//                        progress.dismiss()
//                        toast("" + it.value.message)
//                    }
//                }
//                is Resource.Failure -> {
//                    progress.dismiss()
//                    toast("" + it.exception!!.message)
//                }
//                is Resource.Loading -> {
//                    progress.show()
//                }
//            }
//        })
//    }
}