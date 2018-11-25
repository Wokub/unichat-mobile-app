package com.wk.unichat.WebRequests

object UserData {

    var id = ""
    var email = ""
    var name = ""
    var avatarColor = ""
    var avatarName = ""

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