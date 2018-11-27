package com.wk.unichat.Ctrl

import android.content.*
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.wk.unichat.R
import com.wk.unichat.Utils.BROADCAST_USER_UPDATE
import com.wk.unichat.WebRequests.Requests
import com.wk.unichat.WebRequests.UserData
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        // Rejestracja "Broadcastu"
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataReceiver,
                IntentFilter(BROADCAST_USER_UPDATE))
    }

    // Update UI
    private val userDataReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (Requests.isLogged) {
                userNameNavHeader.text = UserData.name
                userEmailNavHeader.text = UserData.email

                val resourceID = resources.getIdentifier(UserData.avatarName, "drawable",
                        packageName)
                userImageNavHeader.setImageResource(resourceID)

                loginBtnNavHeader.text = "WYLOGUJ"
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun loginBtnClicked(view: View) {

        // RESET DANYCH
        if(Requests.isLogged) {
            UserData.userLogout()  // Wylogowanie
            userNameNavHeader.text = ""
            userEmailNavHeader.text = ""
            userImageNavHeader.setImageResource(R.drawable.light0)
            loginBtnNavHeader.text = "ZALOGUJ SIĘ"
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent);
        }
    }

    // Tworzenie Dialog Alert pozwalającego na tworzenie nowych kanałów
    // Większość z https://developer.android.com/guide/topics/ui/dialogs
    fun addChannelClicked(view: View) {
        if(Requests.isLogged) {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.adding_channel, null) // Tworzymy View z XML

            builder.setView(dialogView)
                    .setPositiveButton(R.string.create) { dialog, id ->
                        // Wydobywanie textu z dialog alert
                        val nameText = dialogView.findViewById<EditText>(R.id.addChannelID)
                        val infoText = dialogView.findViewById<EditText>(R.id.addChannelInfo)
                        val channelName = nameText.text.toString() // Domyślnie jest ciągiem charów
                        val channelInfo = infoText.text.toString()

                        // Tworzenie kanału


                    }
                    .setNegativeButton(R.string.cancel) { dialog, id ->
                    }
                    .show()
        }
    }

    fun sendMessageBtnClicked(view: View) {
        keyboardShowUpHandler()
    }

    // Metoda ukrywająca klawiaturę
    fun keyboardShowUpHandler () {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken,0)
        }
    }
}
