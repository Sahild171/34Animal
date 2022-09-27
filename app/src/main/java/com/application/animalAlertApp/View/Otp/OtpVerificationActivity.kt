package com.application.animalAlertApp.View.Otp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.application.animalAlertApp.Interfaces.ChangePhoneInterface
import com.application.animalAlertApp.SharedPrefrences.MySharedPreferences
import com.application.animalAlertApp.Utils.MyDialog
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.View.Prefrence.SelectPrefrenceActivity
import com.application.animalAlertApp.databinding.ActivityOtpVerificationBinding
import com.kaopiz.kprogresshud.KProgressHUD
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart


@AndroidEntryPoint
class OtpVerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpVerificationBinding
    private val viewModel: OtpViewModel by viewModels()
    private lateinit var progress: KProgressHUD
    private var mySharedPreferences: MySharedPreferences? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_otp_verification)
        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        mySharedPreferences = MySharedPreferences(this)
        progress = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please Wait")

        doverify()
        changenumber()
        resendotp()
        setOTPAutoFill()
    }

    private fun setOTPAutoFill() {
        val bundle:Bundle = intent.extras!!
        val id = bundle.get("otp")
        binding.firstPinView.setText(id.toString())
    }

    private fun resendotp() {
        binding.apply {
            tvResend.setOnClickListener {
                lifecycleScope.launchWhenStarted {
                    viewModel.resendotp(
                        mySharedPreferences?.getUserid()!!
                    ).catch { e ->
                        toast("" + e.message)
                       // progress.dismiss()
                    }.onStart {
                      //  progress.show()
                    }.collect { response ->
                        if (response.status == 200) {
                         //   progress.dismiss()
                            toast("" + response.message)
                            initTimer()
                            firstPinView.setText(response.otp.toString())
                        } else {
                          //  progress.dismiss()
                            toast("" + response.message)
                        }
                    }
                }
            }
        }
    }


    private fun initTimer() {
        val timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvTime.setText("00:" + millisUntilFinished / 1000)
                binding.tvResend.isEnabled = false
            }
            override fun onFinish() {
                binding.tvTime.setText("00:00")
                binding.tvResend.isEnabled = true
            }
        }
        timer.start()
    }


    private fun doverify() {
        binding.apply {
            btVerify.setOnClickListener {
                if (validation()) {
                    lifecycleScope.launchWhenStarted {
                        viewModel.verifyotp(
                            mySharedPreferences?.getUserid()!!, firstPinView.text.toString()
                        ).catch { e ->
                            toast("" + e.message)
                            progress.dismiss()
                        }.onStart {
                            progress.show()
                        }.collect { response ->
                            progress.dismiss()
                            if (response.status == 200) {
                                toast("" + response.message)
                                val intent =
                                    Intent(this@OtpVerificationActivity, SelectPrefrenceActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                toast("" + response.message)
                                progress.dismiss()
                            }
                        }
                    }
                }
            }
        }
    }


    private fun validation(): Boolean {
        if (binding.firstPinView.text.toString().isEmpty()) {
            toast("Please Enter OTP")
            return false
        }
        return true
    }

    private fun changenumber() {
        binding.tvContactno.setOnClickListener {
            val dialog = MyDialog()
            dialog.Dialog_Change_number(this, interafce)
        }
    }


    val interafce = object : ChangePhoneInterface {
        override fun onchange(number: String) {
            doChangePhoneNo(number)
        }
    }

    private fun doChangePhoneNo(number: String) {
        lifecycleScope.launchWhenStarted {
            viewModel.changeno(
                mySharedPreferences?.getUserid()!!, number
            ).catch { e ->
                toast("" + e.message)
                progress.dismiss()
            }.onStart {
                progress.show()
            }.collect { response ->
                if (response.status == 200) {
                    toast("" + response.message)
                    binding.firstPinView.setText(response.otp.toString())
                    progress.dismiss()
                } else {
                    toast("" + response.message)
                    progress.dismiss()
                }
            }
        }
    }


}