package com.application.animalAlertApp.SharedPrefrences

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class UserPreferences(val context: Context) {
    private val applicationContext = context.applicationContext
    private val dataStore: DataStore<Preferences>

    init {
        dataStore = applicationContext.createDataStore(
            name = "app_preferences"
        )
    }


    val getname: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_NAME]
        }

    suspend fun savename(bookmark: String) {
        dataStore.edit { preferences ->
            preferences[KEY_NAME] = bookmark
        }
    }

    val getphone: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_PHONE]
        }

    suspend fun savephone(phone: String) {
        dataStore.edit { preferences ->
            preferences[KEY_PHONE] = phone
        }
    }

    val getemail: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_EMAIL]
        }

    suspend fun saveemail(email: String) {
        dataStore.edit { preferences ->
            preferences[KEY_EMAIL] = email
        }
    }

    val getimage: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_IMAGE]
        }

    suspend fun saveimage(image: String) {
        dataStore.edit { preferences ->
            preferences[KEY_IMAGE] = image
        }
    }

    val getlocation: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_LOCATION]
        }

    suspend fun savelocation(loc: String) {
        dataStore.edit { preferences ->
            preferences[KEY_LOCATION] = loc
        }
    }

    val getprefrence: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_PREFRENCES]
        }

    suspend fun saveprefrence(prefrencess: String) {
        dataStore.edit { preferences ->
            preferences[KEY_PREFRENCES] = prefrencess
        }
    }

    companion object {
        val KEY_NAME = preferencesKey<String>("key_UserName")
        val KEY_PHONE = preferencesKey<String>("key_UserPhone")
        val KEY_IMAGE = preferencesKey<String>("key_UserImage")
        val KEY_EMAIL = preferencesKey<String>("key_UserEmail")
        val KEY_LOCATION = preferencesKey<String>("key_UserLocation")
        val KEY_PREFRENCES = preferencesKey<String>("key_Prefrences")
    }
}