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
    }

    fun createUserClicked(view: View) {

        activityViewUpdate(true)
        val userName = createUserNameTxt.text.toString()
        val email = createEmailTxt.text.toString()
        val password = createPasswordTxt.text.toString()

        // Sprawdzanie, czy są dane
        if(userName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            Requests.regUser(this, email, password) { registerSuccess->
                if(registerSuccess) { // Jeśli OK, wtedy logujemy
                    Requests.loginUser(this, email, password) {loginSuccess->
                        if(loginSuccess) { // Jeśli OK, wtedy tworzymy użytkownika
                            Requests.createUser(this, userName, email, userAvatar, avatarColor) {success->
                                if (success) {
                                    //Przekazujemy dane do inncyh aktywności
                                    val userDataUpdated = Intent(BROADCAST_USER_UPDATE)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataUpdated) // Udostępniamy informację innym aktywnościom

                                    activityViewUpdate(false)
                                    finish() // Cofnięcie do poprzedniej aktywności
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

    // Informacje o błędzie

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
            loadingMetter.visibility = View.VISIBLE
        } else {
            loadingMetter.visibility = View.INVISIBLE
        }
        createUserBtn.isEnabled = !on
        createUserBtn.isEnabled = !on
    }

}
