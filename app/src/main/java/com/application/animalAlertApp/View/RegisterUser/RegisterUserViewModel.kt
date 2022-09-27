package com.application.animalAlertApp.View.RegisterUser

import android.content.Context
import androidx.lifecycle.ViewModel
import com.application.animalAlertApp.SharedPrefrences.MySharedPreferences
import com.application.animalAlertApp.SharedPrefrences.UserPreferences
import com.application.animalAlertApp.data.Repositry.RegisterUserRepositry
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class RegisterUserViewModel
@Inject constructor(private val repositry: RegisterUserRepositry):ViewModel(){


    fun register(
        phoneNo: String,
        email: String,
        name: String,
        location :String,
        password :String,
        dialCode:String,
        lat:String,
        longi:String,
        deviceToken:String
    ) = repositry.register(phoneNo, email, name, location, password,dialCode,lat, longi,deviceToken)



    fun sociallogin(
        email: String,
        name: String,
        deviceToken:String
    ) = repositry.sociallogin(email, name, deviceToken)


    fun saveCredentials(context: Context,userid: String,token: String,name: String,devicetoken:String){
        val mySharedPreferences= MySharedPreferences(context)
        mySharedPreferences.setUserid(userid)
        mySharedPreferences.setToken(token)
        mySharedPreferences.setUserName(name)
        mySharedPreferences.setdevicetoken(devicetoken)
    }

    suspend fun saveCredentials1(context: Context,name: String,email:String,phone:String,location:String,image:String) {
        val userPreferences= UserPreferences(context)
        userPreferences.saveimage(image)
        userPreferences.savename(name)
        userPreferences.saveemail(email)
        userPreferences.savephone(phone)
        userPreferences.savelocation(location)
    }

}