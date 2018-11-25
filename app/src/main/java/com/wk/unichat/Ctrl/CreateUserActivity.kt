package com.wk.unichat.Ctrl

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.wk.unichat.R
import com.wk.unichat.WebRequests.Requests
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "light0"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    //Android volley!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
    }

    //TODO: avatar do usuniecia ew.
    fun generateUserAvatar(view: View) {
        val randomAvatar = Random()
        val avatar = randomAvatar.nextInt(14)

        userAvatar = "light$avatar"

        val resourcesID = resources.getIdentifier(userAvatar, "drawable", packageName)
        createAvatarView.setImageResource(resourcesID)
    //TODO: Kolorki
/*
        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)

        createAvatarView.setBackgroundColor(Color.rgb(r,g,b))

        val R = r.toDouble() / 255
        val G = g.toDouble() / 255
        val B = b.toDouble() / 255

        avatarColor = "[$R, $G, $B, 1]"
*/
    }

    /*
    //TODO: avatar bg do usuniecia
    fun generateBackgroundClicked(view: View) {

        /*
        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)

        createAvatarView.setBackgroundColor(Color.rgb(r,g,b))

        val R = r.toDouble() / 255
        val G = g.toDouble() / 255
        val B = b.toDouble() / 255

        avatarColor = "[$R, $G, $B, 1]"
        println(avatarColor)
         */
    }
*/
    fun createUserClicked(view: View) {
        val email = createEmailTxt.text.toString()
        val password = createPasswordTxt.text.toString()

        Requests.createUser(this, email, password) {registerSuccess->
            if(registerSuccess) {
                Requests.loginUser(this, email, password) {loginSuccess->
                    if(loginSuccess) {
                        Log.d("Request token:",  Requests.logToken)
                        Log.d("Request email:",  Requests.usrEmail)
                    }
                }
            }
        }
    }

}
