package com.wk.unichat.WebRequests

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.wk.unichat.Utils.URL_REGISTER
import org.json.JSONObject

// TWORZENIE POST REQUEST
object Requests {

    // registerUser
    fun createUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {

        // JSON Body
        val jsonBody = JSONObject()
        // Tworzymy key-e
        jsonBody.put("email", email)
        jsonBody.put("password", password)

        // Web Request
        val requestBody = jsonBody.toString() //zmieniamy na stringa

        // Method Type
        val creationRequest = object : StringRequest(Method.POST, URL_REGISTER, Response.Listener { response -> // Response
            println(response)
            complete(true)
        }, Response.ErrorListener { error -> // Error Response
            Log.d("Error", "Creation fail $error" )
            complete(false)
        }){
            override fun getBodyContentType(): String { // Content Type
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray { // Queque
                return requestBody.toByteArray()
            }
        }

        Volley.newRequestQueue(context).add(creationRequest)
    }
}