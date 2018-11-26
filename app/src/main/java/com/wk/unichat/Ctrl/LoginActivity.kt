package com.wk.unichat.Ctrl

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.wk.unichat.R
import com.wk.unichat.WebRequests.Requests
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginMetter.visibility = View.INVISIBLE
    }

    fun loginBtnClicked(view: View) {

        activityViewUpdate(true)

        val email = loginEmailTxt.text.toString()
        val password = loginPasswordTxt.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            Requests.loginUser(this, email, password) {success->
                if(success) {
                    Requests.findUser(this) {findingSuccessful->
                        if(findingSuccessful) {
                            activityViewUpdate(false)
                            finish()
                        } else {
                            errorInfo()
                        }
                    }
                } else {
                    errorInfo()
                }
            }
        } else {
            errorData()
        }
    }

    fun createUserClicked(view: View) {
        val createUserIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserIntent)
        finish() // Nastepna aktywnosc powroci od razu do MainActivity przy finish()
    }

    fun errorInfo() {
        Toast.makeText(this,"Błąd, spróbuj ponownie.", Toast.LENGTH_LONG).show()
        activityViewUpdate(false)
    }

    fun errorData() {
        Toast.makeText(this,"Za mało danych.", Toast.LENGTH_LONG).show()
        activityViewUpdate(false)
    }

    // Efekty WOW

    fun activityViewUpdate(on: Boolean){
        // Po wciśnięciu dajemy OFF
        if(on) {
            loginMetter.visibility = View.VISIBLE
        } else {
            loginMetter.visibility = View.INVISIBLE
        }

        loginLoginBtn.isEnabled = !on
        loginCreateUserBtn.isEnabled = !on
    }
}
