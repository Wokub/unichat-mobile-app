package com.wk.unichat.WebRequests

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.wk.unichat.Channels.Channel
import com.wk.unichat.Channels.Msg
import com.wk.unichat.Ctrl.App
import com.wk.unichat.Utils.URL_GET_CHANNELS
import com.wk.unichat.Utils.URL_GET_MESSAGES
import org.json.JSONException

object MsgService {
    // ArrayList's containing Channels and Messages
    val channels = ArrayList<Channel>()
    val messages = ArrayList<Msg>()


    fun channels (context: Context, complete: (Boolean) -> Unit) {

        // Pytamy o objekt typu JsonArray
        val channelsReg = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null, Response.Listener {response->

            try {
                // Loop over all channels contained in our database
                for ( i in 0 until response.length()) {
                    val channel = response.getJSONObject(i)     // Variable containing JSON object of every database

                    val name = channel.getString("name")
                    val channelInfo = channel.getString("description")
                    val channelId = channel.getString("_id")

                    val createChannel = Channel(name, channelInfo, channelId)

                    this.channels.add(createChannel)    // Adding channels into our ArrayList<Channel> variable
                }
                complete(true)
            } catch(e: JSONException) {
                Log.d("JSON", "Exception: " + e.localizedMessage)
                complete(false)
            }

        }, Response.ErrorListener {error->
            Log.d("ERROR", "Channels loading fail")
            complete(false)
        }) {
            // Encryption type
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            // Volley library method checking if user is authorized
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.sharedPreferences.authToken}")
                return headers
            }
        }
        Volley.newRequestQueue(context).add(channelsReg) // Creating new request using Volley library
    }

    fun getMsg(context: Context, channelId: String, complete: (Boolean) -> Unit) {

        val url = "$URL_GET_MESSAGES$channelId"

        val messageRequest = object : JsonArrayRequest(Method.GET, url, null, Response.Listener {response ->  
            clearMsg()
            try {
                for (x in 0 until response.length()) {
                    val msg = response.getJSONObject(x) // Variable containing JSON object of every database

                    val msgBody = msg.getString("messageBody")
                    val channelId = msg.getString("channelId")
                    val id = msg.getString("_id")
                    val userName = msg.getString("userName")
                    val userAvatar = msg.getString("userAvatar")
                    val userAvatarColor = msg.getString("userAvatarColor")
                    val timeStamp = msg.getString("timeStamp")

                    val newMessage = Msg(msgBody, userName, channelId, userAvatar, userAvatarColor, id, timeStamp)

                    this.messages.add(newMessage) // Adding messages into our ArrayList<Msg> variable
                }
                complete(true)
            } catch(e: JSONException) {
                complete(false)
            }

        }, Response.ErrorListener {
            complete(false)
        }) {
            // Encryption type
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            // Volley library method checking if user is authorized
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.sharedPreferences.authToken}")
                return headers
            }
        }
        Volley.newRequestQueue(context).add(messageRequest) // Creating new request using Volley library
    }

    fun clearMsg () {
        messages.clear()
    }

}