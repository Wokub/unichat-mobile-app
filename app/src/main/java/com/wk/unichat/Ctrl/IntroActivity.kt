package com.wk.unichat.Ctrl

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.wk.unichat.R

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        loginSwap()
    }

    // Method allowing us to change activity after delay
    private fun loginSwap () {
        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        },5000)
    }

    override fun onRestart() {
        loginSwap()
        super.onRestart()
    }
}
