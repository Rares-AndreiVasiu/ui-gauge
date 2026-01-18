package com.example.gitgauge.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.gitgauge.MainActivity

class CallbackActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
