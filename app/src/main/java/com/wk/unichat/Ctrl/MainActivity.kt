package com.wk.unichat.Ctrl

import android.content.*
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import com.wk.unichat.Channels.Channel
import com.wk.unichat.Channels.Msg
import com.wk.unichat.R
import com.wk.unichat.Utils.BROADCAST_USER_UPDATE
import com.wk.unichat.Utils.SOCKET_URL
import com.wk.unichat.WebRequests.MsgService
import com.wk.unichat.WebRequests.Requests
import com.wk.unichat.WebRequests.UserData
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity(){

    val socket = IO.socket(SOCKET_URL)

    // Wczytywanie kanałów do listy
    lateinit var adapter: ArrayAdapter<Channel>

    var selectedChannel : Channel? = null

    private fun adaptersSetup () {
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MsgService.channels)
        channels.adapter = adapter
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        socket.connect()
        socket.on("channelCreated", newChannel)
        socket.on("messageCreated", newMessage)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataReceiver,
                IntentFilter(BROADCAST_USER_UPDATE))
        adaptersSetup()

        channels.setOnItemClickListener { parent, view, position, id ->
            selectedChannel = MsgService.channels[position]
            drawer_layout.closeDrawer(GravityCompat.START)
            downloadChannelData()
        }
    }
/*
    override fun onResume() {
        // Rejestracja "Broadcastu"
        /*
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataReceiver,
                IntentFilter(BROADCAST_USER_UPDATE))
        */

        super.onResume()
    }
*/


    // Rozłączenie z socketem
    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataReceiver)
        super.onDestroy()
    }

    // Update UI
    private val userDataReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (Requests.isLogged) {
                userNameNavHeader.text = UserData.name
                userEmailNavHeader.text = UserData.email

                val resourceID = resources.getIdentifier(UserData.avatarName, "drawable",
                        packageName)
                userImageNavHeader.setImageResource(resourceID)

                loginBtnNavHeader.text = "WYLOGUJ"

                MsgService.channels(context) {success->
                    if(success) {
                        if(MsgService.channels.count() > 0) {
                            selectedChannel = MsgService.channels[0] // Domyślny kanał
                            adapter.notifyDataSetChanged() // Sprawdzamy, czy pojawiły się kanały i odświeżamy
                            downloadChannelData()
                        }
                    }
                }
            }
        }
    }


    fun downloadChannelData () {
        mainChannelName.text = "${selectedChannel?.name}"

        //pobieranie wiadomosci
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

            MsgService.channels.clear()
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }


    // WEB SOCKET - łączenie między już połączonymi użytkownikami

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
                        socket.emit("newChannel", channelName, channelInfo)
                    }
                    .setNegativeButton(R.string.cancel) { dialog, id ->
                    }
                    .show()
        }
    }

    fun sendMessageBtnClicked(view: View) {
        if(Requests.isLogged && messageTextField.text.isNotEmpty() && selectedChannel != null) { // Może być źle przez brak 90 (aktualnie 92)

            val usrId = UserData.id
            val channelId = selectedChannel!!.id
            socket.emit("newMessage", messageTextField.text.toString(), usrId, channelId, UserData.name,
                    UserData.avatarName, UserData.avatarColor)
            messageTextField.text.clear()
            keyboardShowUpHandler()
        }
    }

    // Metoda ukrywająca klawiaturę
    fun keyboardShowUpHandler () {
        //TODO: val inputManager
        val input = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(input.isAcceptingText) {
            input.hideSoftInputFromWindow(currentFocus.windowToken,0)
        }
    }

    // Threads
    private val newChannel = Emitter.Listener {args ->
        // Wyłączanie blokowania innych wątków poprzez listenera
        runOnUiThread {
            // Wyczytanie danych z emitera (naszej bazy danych)
            val channelName = args[0] as String
            val channelDescription = args[1] as String
            val channelId = args[2] as String

            //Tworzenie instancji kanału
            val newChannel = Channel(channelName, channelDescription, channelId)
            MsgService.channels.add(newChannel)
            adapter.notifyDataSetChanged() // Aktualizuje kanały w danym momencie

            Log.d("TAG", "Channel Test " + newChannel.name + " " + newChannel.info + " " + newChannel.id)
        }
    }

    private val newMessage = Emitter.Listener {args ->
        // Wyłączanie blokowania innych wątków poprzez listenera
        runOnUiThread {
            // Wyczytanie danych z emitera (naszej bazy danych)
            val msgBody = args[0] as String
            val channelId = args[2] as String
            val usrName = args[3] as String
            val usrAvatar = args[4] as String
            val avatarColor = args[5] as String
            val id = args[6] as String
            val timeStamp = args[7] as String


            //Tworzenie instancji kanału
            val newMsg = Msg(msgBody, usrName, channelId, usrAvatar, avatarColor, id, timeStamp)

            MsgService.messages.add(newMsg)

            adapter.notifyDataSetChanged() // Aktualizuje kanały w danym momencie


            Log.d("MESSAGE_TEST", newMsg.msg)
        }
    }
}
