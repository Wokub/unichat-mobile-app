package com.wk.unichat.Ctrl

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
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
        // Hiding login bar
        loginMetter.visibility = View.INVISIBLE
    }

    //
    fun loginBtnClicked(view: View) {
        activityViewUpdate(true)

        val email = loginEmailTxt.text.toString()
        val password = loginPasswordTxt.text.toString()

        // Checking if any of fields is empty
        if (email.isNotEmpty() && password.isNotEmpty()) {
            // Logging in
            Requests.loginUser(this, email, password) {success->
                if(success) {
                    // Getting user characteristics
                    Requests.findUser(this) {findingSuccessful->
                        if(findingSuccessful) {
                            activityViewUpdate(false)
                            // Changing activity into MainActivity, finishing current one
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

    // Create user activity initializer
    fun createUserClicked(view: View) {
        val createUserIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserIntent)
        finish() // Finishing current activity (next activity goes back to the Main Activity)
    }

    fun errorInfo() {
        Toast.makeText(this,R.string.login_error, Toast.LENGTH_LONG).show()
        activityViewUpdate(false)
    }

    fun errorData() {
        Toast.makeText(this,R.string.size_error, Toast.LENGTH_LONG).show()
        activityViewUpdate(false)
    }

    fun activityViewUpdate(on: Boolean){
        if(on) {
            loginMetter.visibility = View.VISIBLE
        } else {
            loginMetter.visibility = View.INVISIBLE
        }

        loginLoginBtn.isEnabled = !on
        loginCreateUserBtn.isEnabled = !on
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        var backButton = true

        if(!backButton) { return super.onKeyDown(keyCode, event) }

        return false
    }
}
