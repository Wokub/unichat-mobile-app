package com.wk.unichat.WebRequests

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.wk.unichat.Utils.*
import org.json.JSONException
import org.json.JSONObject

// TWORZENIE POST REQUEST
object Requests {

    var isLogged = false
    var usrEmail = ""
    var logToken = ""

    // Rejestrowanie użytkownika na podstawie maila i hasła
    fun regUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {

        // JSON Body
        val jsonBody = JSONObject()
        // Tworzymy key-e
        jsonBody.put("email", email)
        jsonBody.put("password", password)

        // Web Request
        val requestBody = jsonBody.toString() //zmieniamy na stringa

        // Method Type
        val requestCreation = object : StringRequest(Method.POST, URL_REGISTER, Response.Listener { response -> // Response
            println(response)
            complete(true)
        }, Response.ErrorListener { error -> // Error Response
            Log.d("Error", "Creation fail $error" )
            complete(false)
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        Volley.newRequestQueue(context).add(requestCreation) // Queque
    }

    // Logowanie użytkownika
    fun loginUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val loginRequest = object: JsonObjectRequest(Method.POST, URL_LOGIN, null, Response.Listener {response->
            // Wyciąganie danych z naszego JSON-a
            Log.d("Success", "Creation completed $response" )

            // Tworzenie wyjątku, np. gdyby brakowało tokena user
            try {
                usrEmail = response.getString("user") // Wyciągamy stringa z el. o tagu "user"
                logToken = response.getString("token")
                isLogged = true
                complete(true)
            } catch (e: JSONException) {
                Log.d("JSON", "Exception: " + e.localizedMessage)
                complete(false)
            }

        },Response.ErrorListener {error->
            // Error
            Log.d("ERROR", "Creation fail $error" )
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        Volley.newRequestQueue(context).add(loginRequest)
    }

    // Tworzenie użytkowika
    fun createUser(context: Context, name: String, email: String, avatarName: String, avatarColor: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("name", name)
        jsonBody.put("email", email)
        jsonBody.put("avatarName", avatarName)
        jsonBody.put("avatarColor", avatarColor)

        val requestBody = jsonBody.toString()

        val requestCreation = object : JsonObjectRequest(Method.POST, URL_CREATE_USER, null, Response.Listener { response ->
            try{
                UserData.name = response.getString("name")
                UserData.email = response.getString("email")
                UserData.avatarName = response.getString("avatarName")
                UserData.avatarColor = response.getString("avatarColor")
                UserData.id = response.getString("_id")
                complete(true)
            } catch(e: JSONException) {
                Log.d("JSON", "Exception" + e.localizedMessage)
                complete(false)
            }
        }, Response.ErrorListener {error ->
            Log.d("ERROR", "Creation fail $error" )
            complete(false)
        }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>() // Pary kluczy
                headers.put("Authorization", "Bearer $logToken") // Bearer - MongoDB
                return headers
            }
        }
        Volley.newRequestQueue(context).add(requestCreation)
    }

    //  complete: (Boolean) -> Unit lambda zwracająca
    fun findUser(context: Context, complete: (Boolean) -> Unit) { // Szukanie użytkownika za pomocą maila
        val findUserRequest = object: JsonObjectRequest(Method.GET, "$URL_GET_USER$usrEmail",
                null, Response.Listener {response->

            try {
                UserData.name = response.getString("name")
                UserData.email = response.getString("email")
                UserData.avatarName = response.getString("avatarName")
                UserData.avatarColor = response.getString("avatarColor")
                UserData.id = response.getString("_id")

                val userDataSwap = Intent(BROADCAST_USER_UPDATE)
                LocalBroadcastManager.getInstance(context).sendBroadcast(userDataSwap)
                complete(true)

            } catch (e: JSONException) {
                Log.d("JSON", "Exception" + e.localizedMessage)
                complete(false)
            }

        }, Response.ErrorListener {error->
            Log.d("ERROR", "No such user")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>() // Pary kluczy
                headers.put("Authorization", "Bearer $logToken") // Bearer - MongoDB
                return headers
            }
        }

        Volley.newRequestQueue(context).add(findUserRequest)
    }
}