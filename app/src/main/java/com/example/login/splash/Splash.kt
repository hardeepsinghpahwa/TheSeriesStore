package com.example.login.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.*
import androidx.databinding.DataBindingUtil
import com.victor.loading.rotate.RotateLoading
import com.example.login.R
import com.example.login.databinding.ActivitySplashBinding

class Splash : AppCompatActivity() {
    lateinit var rotateLoading: RotateLoading
    lateinit var binding:ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=DataBindingUtil.setContentView(this,R.layout.activity_splash)
        binding.executePendingBindings()

        binding.rotateloading.start()

        val handler= Handler(Looper.getMainLooper())
        handler.postDelayed({
            //startActivity(Intent(this@Splash, Home::class.java))
        }, 4000)
    }
}