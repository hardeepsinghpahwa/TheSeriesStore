package com.example.login


import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.os.Bundle
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import android.content.Intent
import maes.tech.intentanim.CustomIntent

import android.os.Build
import android.view.*
import androidx.databinding.DataBindingUtil
import com.example.login.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.executePendingBindings()
        binding.login.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                startActivity(Intent(this@MainActivity, Login::class.java))
                CustomIntent.customType(this@MainActivity, "fadein-to-fadeout")
            }
        })
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.child("name").exists()) {
                            startActivity(Intent(this@MainActivity, Home::class.java))
                            finish()
                            CustomIntent.customType(this@MainActivity, "fadein-to-fadeout")
                        } else if (snapshot.child("phone").exists()) {
                            val intent: Intent = (Intent(this@MainActivity, ProfileSetup::class.java))
                            intent.putExtra("phone", snapshot.child("phone").getValue(String::class.java))
                            startActivity(intent)
                            finish()
                            CustomIntent.customType(this@MainActivity, "left-to-right")
                        }
                    } else {
                        val intent: Intent = (Intent(this@MainActivity, ProfileSetup::class.java))
                        intent.putExtra("phone", snapshot.child("phone").getValue(String::class.java))
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                        CustomIntent.customType(this@MainActivity, "left-to-right")
                    }
                }

                public override fun onCancelled(error: DatabaseError) {}
            })
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
        binding.newlogin.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                startActivity(Intent(this@MainActivity, NewLogin::class.java))
                CustomIntent.customType(this@MainActivity, "fadein-to-fadeout")
            }
        })
    }
}