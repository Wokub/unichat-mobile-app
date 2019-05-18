package com.wk.unichat.Ctrl

import android.app.Application

// Application has onCreate, so it needs initialization
class App : Application() {

    // Singleton inside specified class
    companion object {
        // One instance of this class
        lateinit var sharedPreferences: SharedPreferences
    }

    override fun onCreate() {
        // Context of entire application
        sharedPreferences = SharedPreferences(applicationContext)
        super.onCreate()
    }
}