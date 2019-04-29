package com.wk.unichat.WebRequests


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
        Requests.logToken = ""
        Requests.usrEmail = ""
        Requests.isLogged = false
    }

}