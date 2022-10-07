package com.example.login


import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.*
import com.victor.loading.rotate.RotateLoading
import android.view.*

class Splash : AppCompatActivity() {
    var rotateLoading: RotateLoading? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        rotateLoading = findViewById<View>(R.id.rotateloading) as RotateLoading?
        rotateLoading!!.start()
        val handler: Handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            public override fun run() {
                startActivity(Intent(this@Splash, Home::class.java))
            }
        }, 4000)
    }
}