package com.wk.unichat.Ctrl

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
        Requests.createUser(this,"282127@uwr.edu.pl", "12345678") {complete->
            if(complete) {

            }
        }
    }

}
