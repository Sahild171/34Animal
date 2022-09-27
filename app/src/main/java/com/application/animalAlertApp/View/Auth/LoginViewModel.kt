package com.application.animalAlertApp.View.Auth


import android.content.Context
import androidx.lifecycle.ViewModel
import com.application.animalAlertApp.SharedPrefrences.MySharedPreferences
import com.application.animalAlertApp.SharedPrefrences.UserPreferences
import com.application.animalAlertApp.data.Repositry.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val authRepository: AuthRepository) : ViewModel() {

    fun login(
        mobile: String,
        password: String,
        deviceToken:String
    ) = authRepository.login(mobile, password,deviceToken)

    fun sociallogin(
        email: String,
        name: String,
        deviceToken:String
    ) = authRepository.sociallogin(email, name, deviceToken)

    fun saveCredentials(context: Context,userid: String,token: String,name: String,devicetoken:String){
        val mySharedPreferences= MySharedPreferences(context)
        mySharedPreferences.setUserid(userid)
        mySharedPreferences.setToken(token)
        mySharedPreferences.setUserName(name)
        mySharedPreferences.setdevicetoken(devicetoken)
    }


    suspend fun saveCredentials1(context: Context,name: String,email:String,image:String,phone:String,location:String,prefrences:String) {
        val userPreferences=UserPreferences(context)
        userPreferences.savename(name)
        userPreferences.saveemail(email)
        userPreferences.saveimage(image)
        userPreferences.savephone(phone)
        userPreferences.savelocation(location)
        userPreferences.saveprefrence(prefrences)
    }

}