package com.application.animalAlertApp.SharedPrefrences


import android.content.Context
import android.content.SharedPreferences


class MySharedPreferences(context: Context) {
    private val preferences: SharedPreferences
    private val editor: SharedPreferences.Editor

    init {
        preferences = context.getSharedPreferences(
            PREFERENCE_NAME, 0
        )
        editor = preferences.edit()
    }


    fun getUserid(): String? {
        return preferences.getString("Userid", "")
    }

    fun setUserid(userid: String) {
        editor.putString("Userid", userid)
        editor.commit()
    }


    fun getToken(): String? {
        return preferences.getString("Token", "")
    }

    fun setToken(token: String) {
        editor.putString("Token", token)
        editor.commit()
    }

    fun getUsername(): String? {
        return preferences.getString("UserName", "")
    }

    fun setUserName(token: String) {
        editor.putString("UserName", token)
        editor.commit()
    }

    fun getdevicetoken(): String? {
        return preferences.getString("DeviceToken", "")
    }

    fun setdevicetoken(token: String) {
        editor.putString("DeviceToken", token)
        editor.commit()
    }


    fun setnotificationStatus(status: Int) {
        editor.putInt("NotificationStatus", status)
        editor.commit()
    }

    fun getnotificationStatus(): Int {
        return preferences.getInt("NotificationStatus", 0)
    }


    fun setshowfirsttime(status: Boolean) {
        editor.putBoolean("Instructions", status)
        editor.commit()
    }

    fun getshowfirsttime(): Boolean {
        return preferences.getBoolean("Instructions", true)
    }


    fun setlocationvisibility(status: Boolean) {
        editor.putBoolean("Location", status)
        editor.commit()
    }

    fun getlocationvisibility(): Boolean {
        return preferences.getBoolean("Location", true)
    }

    companion object {
        private const val PRIVATE_MODE = 0
        private const val PREFERENCE_NAME = "AAC"
    }

}