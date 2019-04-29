package com.wk.unichat.Ctrl

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.wk.unichat.R
import com.wk.unichat.Utils.BROADCAST_USER_UPDATE
import com.wk.unichat.WebRequests.Requests
import com.wk.unichat.WebRequests.UserData
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    // Wartości domyślne
    var userAvatar = "light0"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        loadingMetter.visibility = View.INVISIBLE // Wizualizacja ładowania (na wejściu OFF)
    }

    // Avatar creation
    fun generateUserAvatar(view: View) {
        val randomAvatar = Random()
        val avatar = randomAvatar.nextInt(14)

        userAvatar = "light$avatar"

        //
        val resourcesID = resources.getIdentifier(userAvatar, "drawable", packageName)
        createAvatarView.setImageResource(resourcesID)
    }

    // User creation method
    fun createUserClicked(view: View) {

        activityViewUpdate(true)
        // Creating variables equal to inputs converted to string
        val userName = createUserNameTxt.text.toString()
        val email = createEmailTxt.text.toString()
        val password = createPasswordTxt.text.toString()

        // Checking if all values are given
        if(userName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            Requests.regUser(this, email, password) { registerSuccess->     // Requests method call, with registerSuccess callback (boolean)
                if(registerSuccess) {
                    Requests.loginUser(this, email, password) {loginSuccess->   // We are automatically login into our account, so we have to check if it's possible
                        if(loginSuccess) {
                            Requests.createUser(this, userName, email, userAvatar, avatarColor) {success->  // Creating user for other activities
                                if (success) {

                                    val userDataUpdated = Intent(BROADCAST_USER_UPDATE)
                                    //
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataUpdated)

                                    activityViewUpdate(false)
                                    finish() // Previous activity
                                } else {
                                    errorInfo()
                                }
                            }
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

    //Methods responsible for handling Toast errors info showing up to user
    fun errorInfo() {
        Toast.makeText(this,R.string.login_error, Toast.LENGTH_LONG).show()
        activityViewUpdate(false)
    }

    fun errorData() {
        Toast.makeText(this,R.string.size_error, Toast.LENGTH_LONG).show()
        activityViewUpdate(false)
    }

    // Method responsible for progress bar
    fun activityViewUpdate(on: Boolean){
        if(on) {
            loadingMetter.visibility = View.VISIBLE
        } else {
            loadingMetter.visibility = View.INVISIBLE
        }

        // Deactivation of already clicked buttons
        createUserBtn.isEnabled = !on
        createAvatarView.isEnabled = !on
    }

}
