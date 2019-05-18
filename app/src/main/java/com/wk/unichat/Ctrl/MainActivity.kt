package com.wk.unichat.Ctrl

import android.content.*
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import com.wk.unichat.Adapt.MsgAdapt
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

    val socket = IO.socket(SOCKET_URL)      // Socket.io

    lateinit var adapter: ArrayAdapter<Channel>
    lateinit var msgAdapter: MsgAdapt

    var selectedChannel : Channel? = null

    // Method responsible for loading data and generating View objects based on this data
    private fun adaptersSetup () {
        // Channels adapter
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MsgService.channels)
        channels.adapter = adapter

        // Messages adapter
        msgAdapter = MsgAdapt(this, MsgService.messages)
        messageListView.adapter = msgAdapter

        val layoutManager = LinearLayoutManager(this)
        messageListView.layoutManager = layoutManager
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

        // Getting broadcast data
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataReceiver,
                IntentFilter(BROADCAST_USER_UPDATE))
        // Setting adapter
        adaptersSetup()

        // Checking if we are logged in, so data could be saved into our shared preferences
        if(App.sharedPreferences.isLogged) {
            Requests.findUser(this) {}
        }

        // Channel change handler
        channels.setOnItemClickListener { parent, view, position, id ->
            selectedChannel = MsgService.channels[position]
            drawer_layout.closeDrawer(GravityCompat.START)
            downloadChannelData()
        }
    }

    // Turning off back button
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        var backButton = true

        if(!backButton) {
            return super.onKeyDown(keyCode, event)
        }

        return false
    }

    // Turning off socket
    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataReceiver)
        super.onDestroy()
    }

    // Data receiver
    private val userDataReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (App.sharedPreferences.isLogged) {
                userNameNavHeader.text = UserData.name
                userEmailNavHeader.text = UserData.email

                val resourceID = resources.getIdentifier(UserData.avatarName, "drawable",
                        packageName)
                userImageNavHeader.setImageResource(resourceID)

                loginBtnNavHeader.text = "WYLOGUJ"

                MsgService.channels(context) {success->
                    if(success) {
                        if(MsgService.channels.count() > 0) {
                            selectedChannel = MsgService.channels[0]  // Default channel
                            adapter.notifyDataSetChanged()            // Checking if there is any new channel
                            downloadChannelData()
                        }
                    }
                }
            }
        }
    }


    fun downloadChannelData () {
        mainChannelName.text = "${selectedChannel?.name}"

        // Getting messages from channel
        if(selectedChannel != null ) {
            MsgService.getMsg(this, selectedChannel!!.id) {complete->
                if(complete) {
                    msgAdapter.notifyDataSetChanged()

                    // Latest message on bottom
                    if(msgAdapter.itemCount > 0) {
                        messageListView.smoothScrollToPosition(msgAdapter.itemCount - 1)
                    }
                }
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

    // Login button handler
    fun loginBtnClicked(view: View) {
        // Logging out
        if(App.sharedPreferences.isLogged) {
            UserData.userLogout()  // Wylogowanie
            adapter.notifyDataSetChanged()
            msgAdapter.notifyDataSetChanged()
            userNameNavHeader.text = ""
            userEmailNavHeader.text = ""
            userImageNavHeader.setImageResource(R.drawable.light0)
            loginBtnNavHeader.text = "ZALOGUJ SIĘ"
            mainChannelName.text = "Nie zalogowany"

            MsgService.channels.clear()
            MsgService.messages.clear()
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    // Method handling dialog alert
    fun addChannelClicked(view: View) {
        if(App.sharedPreferences.isLogged) {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.adding_channel, null) // Creating View from XML

            builder.setView(dialogView)
                    .setPositiveButton(R.string.create) { dialog, id ->
                        // Getting data from dialog fields
                        val nameText = dialogView.findViewById<EditText>(R.id.addChannelID)
                        val infoText = dialogView.findViewById<EditText>(R.id.addChannelInfo)
                        val channelName = nameText.text.toString() // Domyślnie jest ciągiem charów
                        val channelInfo = infoText.text.toString()

                        // Emitting new channel data into socket
                        socket.emit("newChannel", channelName, channelInfo)
                    }
                    .setNegativeButton(R.string.cancel) { dialog, id ->
                    }
                    .show()
        }
    }

    fun sendMessageBtnClicked(view: View) {
        if(App.sharedPreferences.isLogged && messageTextField.text.isNotEmpty() && selectedChannel != null) {
            val usrId = UserData.id
            val channelId = selectedChannel!!.id

            // Emit an event to the socket
            socket.emit("newMessage", messageTextField.text.toString(), usrId, channelId, UserData.name,
                    UserData.avatarName, UserData.avatarColor)
            messageTextField.text.clear()

            keyboardShowUpHandler() // Hiding keyboard
        }
    }

    // Keyboard hiding
    fun keyboardShowUpHandler () {

        val input = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(input.isAcceptingText) {
            input.hideSoftInputFromWindow(currentFocus.windowToken,0)
        }
    }

    // Threads
    private val newChannel = Emitter.Listener {args ->

        if(App.sharedPreferences.isLogged){
            // Turning off blocking of other threads by listener
            runOnUiThread {
                // Loading data from our emit
                val channelName = args[0] as String
                val channelDescription = args[1] as String
                val channelId = args[2] as String

                val newChannel = Channel(channelName, channelDescription, channelId)
                // Adding new channel
                MsgService.channels.add(newChannel)
                // Notification about change
                adapter.notifyDataSetChanged()

                Log.d("TAG", "Channel Test " + newChannel.name + " " + newChannel.info + " " + newChannel.id)
            }
        }
    }

    private val newMessage = Emitter.Listener {args ->
        if(App.sharedPreferences.isLogged) {
            // Turning off blocking of other threads by listener
            runOnUiThread {
                // Loading data from our emit
                val channelId = args[2] as String
                if(channelId == selectedChannel?.id) {
                    val msgBody = args[0] as String
                    val usrName = args[3] as String
                    val usrAvatar = args[4] as String
                    val avatarColor = args[5] as String
                    val id = args[6] as String
                    val timeStamp = args[7] as String


                    // Creating instance of message
                    val newMsg = Msg(msgBody, usrName, channelId, usrAvatar, avatarColor, id, timeStamp)

                    // Adding new message
                    MsgService.messages.add(newMsg)
                    // Channel update
                    adapter.notifyDataSetChanged()
                    Log.d("MESSAGE_TEST", newMsg.msg)
                    // Notification about change
                    msgAdapter.notifyDataSetChanged()
                    // Messages listed : latest > bottom
                    messageListView.smoothScrollToPosition(msgAdapter.itemCount -1)
                }
            }
        }
    }
}
