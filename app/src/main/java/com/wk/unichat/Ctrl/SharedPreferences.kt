package com.wk.unichat.Ctrl

import android.content.Context
import android.content.SharedPreferences

// Class that allows user to set some data that will be saved later on device,
// so he won't need to log everytime he turns app on

// TIP: It's turned on before any activity starts, inside manifest
class SharedPreferences(context: Context) {
    val FILENAME = "preferences"
    val preferences: SharedPreferences = context.getSharedPreferences(FILENAME, 0)

    val IS_LOGGED = "isLogged"
    val AUTH_TOKEN = "authToken"
    val USR_MAIL = "usrMail"

    // Getter/Setter
    var isLogged: Boolean
        get() = preferences.getBoolean(IS_LOGGED, false)
        set(value) = preferences.edit().putBoolean(IS_LOGGED, value).apply()

    var authToken: String
        get() = preferences.getString(AUTH_TOKEN, "")
        set(value) = preferences.edit().putString(AUTH_TOKEN, value).apply()

    var usrEmail: String
        get() = preferences.getString(USR_MAIL, "")
        set(value) = preferences.edit().putString(USR_MAIL, value).apply()
}