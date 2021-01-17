package com.sneyder.biznearby.data.preferences

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppPreferencesHelper @Inject constructor(sharedPreferences: SharedPreferences): PreferencesHelper(sharedPreferences){
    companion object {
        const val USER = "user"
        const val ACCESS_TOKEN = "Authorization"
    }

}