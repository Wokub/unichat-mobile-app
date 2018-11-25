package com.wk.unichat.Ctrl

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.wk.unichat.R
import com.wk.unichat.WebRequests.Requests
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun loginBtnClicked(view: View) {
        val email = loginEmailTxt.text.toString()
        val password = loginPasswordTxt.text.toString()

        Requests.loginUser(this,email,password) {success->
            if(success) {
                Requests.findUser(this) {findingSuccessful->
                    if(findingSuccessful) {
                        finish()
                    }
                }
            }

        }
    }

    fun createUserClicked(view: View) {
        val createUserIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserIntent)
        finish() // Nastepna aktywnosc powroci od razu do MainActivity przy finish()
    }
}
