package com.wk.unichat.WebRequests

import com.wk.unichat.Ctrl.App


object UserData {
    // Variables initialized with empty strings
    var id = ""
    var email = ""
    var name = ""
    var avatarColor = ""
    var avatarName = ""

    // Method changing all variables containing data into empty strings
    fun userLogout() {
        id = ""
        email = ""
        name = ""
        avatarColor = ""
        avatarName = ""
        App.sharedPreferences.authToken = ""
        App.sharedPreferences.usrEmail = ""
        App.sharedPreferences.isLogged = false
    }

}