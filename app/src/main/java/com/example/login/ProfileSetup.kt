package com.example.login

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.os.Bundle
import com.irozon.sneaker.Sneaker
import dmax.dialog.SpotsDialog
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import android.content.Intent
import maes.tech.intentanim.CustomIntent
import com.google.android.material.textfield.TextInputEditText
import com.valdesekamdem.library.mdtoast.MDToast
import android.text.TextUtils
import com.google.firebase.auth.AuthResult
import android.app.AlertDialog
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import android.util.Log
import android.util.Patterns
import android.view.*
import com.google.android.gms.tasks.Task
import java.util.*

class ProfileSetup: AppCompatActivity() {
    lateinit var name: TextInputEditText
    lateinit var email: TextInputEditText
    lateinit var phone: TextInputEditText
    lateinit var password: TextInputEditText
    lateinit var confirmpassword: TextInputEditText
    lateinit var createaccount: TextView
    lateinit var login: TextView
    var i: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup)
        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        phone = findViewById(R.id.phone)
        password = findViewById(R.id.password)
        confirmpassword = findViewById(R.id.repeatpassword)
        createaccount = findViewById(R.id.createaccount)
        login = findViewById(R.id.login)
        phone.setKeyListener(null)
        phone.setText(getIntent().getStringExtra("phone"))
        createaccount.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                if ((name.getText().toString() == "")) {
                    Sneaker.with(this@ProfileSetup)
                            .setTitle("Enter you name", R.color.white)
                            .setMessage("Name cant be empty", R.color.white)
                            .setIcon(R.drawable.info, R.color.white)
                            .setDuration(2000)
                            .autoHide(true)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200)
                } else if ((email.getText().toString() == "")) {
                    Sneaker.with(this@ProfileSetup)
                            .setTitle("Enter you email", R.color.white)
                            .setMessage("Email cant be empty", R.color.white)
                            .setIcon(R.drawable.info, R.color.white)
                            .setDuration(2000)
                            .autoHide(true)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200)
                } else if (!isValidEmail(email.getText().toString())) {
                    Sneaker.with(this@ProfileSetup)
                            .setTitle("Invalid Email", R.color.white)
                            .setMessage("Enter a valid email", R.color.white)
                            .setDuration(2000)
                            .autoHide(true)
                            .setIcon(R.drawable.info, R.color.white)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200)
                } else if ((password.getText().toString() == "")) {
                    Sneaker.with(this@ProfileSetup)
                            .setTitle("Enter you password", R.color.white)
                            .setMessage("Password cant be empty", R.color.white)
                            .setDuration(2000)
                            .setIcon(R.drawable.info, R.color.white)
                            .autoHide(true)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200)
                } else if (password.getText().toString().length < 8) {
                    Sneaker.with(this@ProfileSetup)
                            .setTitle("Short Password", R.color.white)
                            .setMessage("Password must be minimum of 8 length", R.color.white)
                            .setIcon(R.drawable.info, R.color.white)
                            .setDuration(2000)
                            .autoHide(true)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200)
                } else if (!containsdigit(password.getText().toString())) {
                    Sneaker.with(this@ProfileSetup)
                            .setTitle("Invalid Password", R.color.white)
                            .setMessage("Password must contain a number", R.color.white)
                            .setDuration(2000)
                            .setIcon(R.drawable.info, R.color.white)
                            .autoHide(true)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200)
                } else if (!(confirmpassword.getText().toString() == password.getText().toString())) {
                    Sneaker.with(this@ProfileSetup)
                            .setTitle("Passwords Do Not Match", R.color.white)
                            .setMessage("Try Again Please!", R.color.white)
                            .setDuration(2000)
                            .setIcon(R.drawable.info, R.color.white)
                            .autoHide(true)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200)
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Profiles").addListenerForSingleValueEvent(object : ValueEventListener {
                        public override fun onDataChange(snapshot: DataSnapshot) {
                            i = 0
                            for (dataSnapshot: DataSnapshot in snapshot.getChildren()) {
                                if (Objects.equals(dataSnapshot.child("email").getValue(String::class.java), email.getText().toString())) {
                                    i++
                                }
                            }
                            if (i > 0) {
                                Sneaker.with(this@ProfileSetup)
                                        .setTitle("Email Already Registered", R.color.white)
                                        .setMessage("Please Use A Different Email", R.color.white)
                                        .setIcon(R.drawable.info, R.color.white)
                                        .setDuration(2000)
                                        .autoHide(true)
                                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                        .setCornerRadius(10, 0)
                                        .sneak(R.color.teal_200)
                                /*final Handler handler = new Handler(Looper.getMainLooper());
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 2000);*/
                            } else {
                                val credential: AuthCredential = EmailAuthProvider.getCredential(email.getText().toString(), password.getText().toString())
                                val map=mutableMapOf<String?, Any?>()
                                map.put("name", name.getText().toString())
                                map.put("email", email.getText().toString())
                                map.put("phone", phone.getText().toString())
                                map.put("password", password.getText().toString())
                                val alertDialog: AlertDialog = SpotsDialog.Builder()
                                        .setCancelable(false)
                                        .setContext(this@ProfileSetup)
                                        .setTheme(R.style.ProgressDialog)
                                        .setMessage("Saving Your Details")
                                        .build()
                                alertDialog.show()
                                FirebaseAuth.getInstance().getCurrentUser().linkWithCredential(credential)
                                        .addOnCompleteListener(this@ProfileSetup, object : OnCompleteListener<AuthResult?> {
                                            public override fun onComplete(task: Task<AuthResult?>) {
                                                if (task.isSuccessful()) {
                                                    FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(object : OnCompleteListener<Void?> {
                                                        public override fun onComplete(p0: Task<Void?>) {
                                                            if (task.isSuccessful()) {
                                                                alertDialog.dismiss()
                                                                MDToast.makeText(getApplicationContext(), "Account Created Successfully", MDToast.TYPE_SUCCESS, MDToast.LENGTH_SHORT).show()
                                                                startActivity(Intent(this@ProfileSetup, Home::class.java))
                                                                CustomIntent.customType(this@ProfileSetup, "fadein-to-fadeout")
                                                                finish()
                                                            } else {
                                                                alertDialog.dismiss()
                                                                Sneaker.with(this@ProfileSetup)
                                                                        .setTitle("Error Saving Details", R.color.white)
                                                                        .setMessage("Try Again Please!", R.color.white)
                                                                        .setDuration(2000)
                                                                        .setIcon(R.drawable.info, R.color.white)
                                                                        .autoHide(true)
                                                                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                                        .setCornerRadius(10, 0)
                                                                        .sneak(R.color.teal_200)
                                                            }
                                                        }
                                                    })
                                                } else {
                                                    Log.w("TAG", "linkWithCredential:failure", task.getException())
                                                    alertDialog.dismiss()
                                                    Sneaker.with(this@ProfileSetup)
                                                            .setTitle("Error Saving Details", R.color.white)
                                                            .setIcon(R.drawable.info, R.color.white)
                                                            .setMessage("Try Again Please!", R.color.white)
                                                            .setDuration(2000)
                                                            .autoHide(true)
                                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                            .setCornerRadius(10, 0)
                                                            .sneakError()
                                                }
                                            }
                                        })
                            }
                        }

                        public override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }
        })
    }

    fun containsdigit(input: String?): Boolean {
        var containsDigit: Boolean = false
        if (input != null && !input.isEmpty()) {
            for (c: Char in input.toCharArray()) {
                containsDigit = Character.isDigit(c)
                if (containsDigit) {
                    break
                }
            }
        }
        return containsDigit
    }

    companion object {
        fun isValidEmail(target: CharSequence?): Boolean {
            return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches())
        }
    }
}