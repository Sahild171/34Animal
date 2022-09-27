package com.application.animalAlertApp.View.Auth

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.*
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.Utils.checkForInternet
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.View.ForgotPassword.ForgotPasswordActivity
import com.application.animalAlertApp.View.Main.HomeActivity
import com.application.animalAlertApp.View.RegisterUser.SignUpActivity
import com.application.animalAlertApp.View.Prefrence.SelectPrefrenceActivity
import com.application.animalAlertApp.data.Response.Auth.FindUser
import com.application.animalAlertApp.databinding.ActivityLoginBinding
import com.google.android.gms.ads.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val Req_Code: Int = 123
    private var dev_token: String = ""
    private lateinit var pd: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//         setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pd = MyProgressBar.progress(this@LoginActivity)
        gotoSignup()
        ads()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        googlesignin()

        binding.tvForgotpassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.btLogin.setOnClickListener {
            if (checkForInternet(this)) {
                getFCMToken("SimpleLogin")
            } else {
                toast("No Internet connection")
            }
        }

    }


    private fun getFCMToken(type: String) {
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(OnCompleteListener<String?> { task ->
                if (!task.isSuccessful) {
                    Log.w("ExceptionFCM", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                dev_token = task.result
              //  toast(""+dev_token)
                if (type.equals("SimpleLogin")) {
                    doLogin()
                } else {
                    signInGoogle()
                }
            })
    }

    private fun ads() {
        // Initialize the Mobile Ads SDK.
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        binding.adView.adListener = object: AdListener() {
            override fun onAdClicked() {
                Log.e("Admob","Clicked")
                // Code to be executed when the user clicks on an ad.
            }
            override fun onAdClosed() {
                Log.e("Admob","Closed")
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
            override fun onAdFailedToLoad(adError : LoadAdError) {
                Log.e("Admob",""+adError.message)
                // Code to be executed when an ad request fails.
            }
            override fun onAdImpression() {
                Log.e("Admob","admipression")
                // Code to be executed when an impression is recorded
                // for an ad.
            }
            override fun onAdLoaded() {
                Log.e("Admob","Loaded")
                // Code to be executed when an ad finishes loading.
            }
            override fun onAdOpened() {
                Log.e("Admob","Opened")
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        }

    }

    private fun googlesignin() {
        binding.btSignigoogle.setOnClickListener {
            getFCMToken("SocailLogin")
            // signInGoogle()
        }
    }

    private fun doLogin() {
        binding.apply {
            if (validations()) {
                lifecycleScope.launchWhenStarted {
                    viewModel.login(
                        etMobile.text.toString().trim(),
                        etPassword.text.toString().trim(),
                        dev_token).catch { e ->
                        toast("" + e.message)
                        pd.dismiss()
                    }.onStart {
                        pd.show()
                    }.collect { response ->
                        if (response.status == 200) {
                            if (response.findUser.isActive) {
                                viewModel.saveCredentials(
                                    this@LoginActivity,
                                    response.findUser._id,
                                    response.findUser.jwtToken,
                                    response.findUser.name, response.findUser.deviceToken)
                                var prefre = ""
                                if (response.findUser.perference.size > 0) {
                                    prefre = response.findUser.perference.toString()
                                }
                                viewModel.saveCredentials1(
                                    this@LoginActivity,
                                    response.findUser.name,
                                    response.findUser.email,
                                    response.findUser.image,
                                    response.findUser.phoneNo,
                                    response.findUser.location, prefre)
                                pd.dismiss()
                                screennavigation(response.findUser)
                            } else {
                                toast("Your account is suspended, kindly contact at jsteward@animalalertcommunity.org")
                            }
                        } else {
                            pd.dismiss()
                            toast("" + response.message)
                        }
                    }
                }
            }
        }
    }

    private fun socailogin(email: String, name: String) {
        binding.apply {
            lifecycleScope.launchWhenStarted {
                viewModel.sociallogin(email, name, dev_token).catch { e ->
                    toast("" + e.message)
                    pd.dismiss()
                }.onStart {
                    pd.show()
                }.collect { response ->
                    if (response.status == 200) {
                        viewModel.saveCredentials(
                            this@LoginActivity,
                            response.updateToken._id,
                            response.updateToken.jwtToken,
                            response.updateToken.name,
                            response.updateToken.deviceToken)
                        var prefre = ""
                        if (response.updateToken.perference.size > 0) {
                            prefre = response.updateToken.perference.toString()
                        }
                        viewModel.saveCredentials1(
                            this@LoginActivity,
                            response.updateToken.name,
                            response.updateToken.email,
                            response.updateToken.image,
                            response.updateToken.phoneNo,
                            response.updateToken.location,
                            prefre)
                        screennavigation(response.updateToken)
                        pd.dismiss()
                    } else {
                        pd.dismiss()
                        toast("" + response.message)
                    }
                }
            }
        }
    }

    private fun screennavigation(data: FindUser) {
        if (!data.isPerferenceSet) {
            val intent = Intent(this@LoginActivity, SelectPrefrenceActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun validations(): Boolean {
        binding.apply {
            val mobile = etMobile.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if (mobile.isEmpty()) {
                toast("Enter Mobile number")
                etMobile.error = "Enter Mobile number"
                return false
            } else if (password.isEmpty()) {
                toast("Enter password")
                etPassword.error = "Enter password"
                return false
            }
            return true
        }
    }


    private fun gotoSignup() {
        binding.tvSignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, Req_Code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Req_Code) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                socailogin(account.email!!, account.displayName!!)
                //   toast("" + account.email + "" + account.displayName)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }


}


