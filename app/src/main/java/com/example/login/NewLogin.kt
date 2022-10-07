package com.example.login

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import android.os.Bundle
import android.view.View.OnTouchListener
import com.irozon.sneaker.Sneaker
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import android.content.Intent
import maes.tech.intentanim.CustomIntent
import com.google.android.material.textfield.TextInputEditText
import android.widget.ProgressBar
import android.view.View.OnFocusChangeListener
import android.text.Selection
import android.text.TextWatcher
import android.text.Editable
import android.view.*
import android.view.inputmethod.InputMethodManager

class NewLogin : AppCompatActivity() {
    lateinit var sendotp: TextView
    lateinit var phone: TextInputEditText
    lateinit var constraintLayout: ConstraintLayout
    var i: Int = 0
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_login)
        phone = findViewById(R.id.phone)
        sendotp = findViewById(R.id.proceed)
        progressBar = findViewById(R.id.progressbar2)
        findViewById<View>(R.id.back).setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                onBackPressed()
            }
        })
        sendotp.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                if (phone.getText()!!.length == 14) {
                    progressBar.setVisibility(View.VISIBLE)
                    FirebaseDatabase.getInstance().getReference().child("Profiles").addListenerForSingleValueEvent(object : ValueEventListener {
                        public override fun onDataChange(snapshot: DataSnapshot) {
                            i = 0
                            for (dataSnapshot: DataSnapshot in snapshot.getChildren()) {
                                if ((dataSnapshot.child("phone").getValue(String::class.java) == phone.getText().toString().substring(4))) {
                                    i++
                                }
                            }
                            if (i > 0) {
                                progressBar.setVisibility(View.GONE)
                                Sneaker.with(this@NewLogin)
                                        .setTitle("Phone Number Already Registered", R.color.white)
                                        .setMessage("Please Sign In Instead", R.color.white)
                                        .setDuration(2000)
                                        .setIcon(R.drawable.info, R.color.white)
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
                                progressBar.setVisibility(View.GONE)
                                val intent: Intent = Intent(this@NewLogin, VerifyOtp::class.java)
                                intent.putExtra("phone", phone.getText().toString().substring(4))
                                startActivity(intent)
                                CustomIntent.customType(this@NewLogin, "left-to-right")
                            }
                        }

                        public override fun onCancelled(error: DatabaseError) {}
                    })
                } else {
                    //MDToast.makeText(NewLogin.this, "Enter a valid phone number", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                    Sneaker.with(this@NewLogin)
                            .setTitle("Invalid Phone Number", R.color.white)
                            .setMessage("Enter a valid phone number!", R.color.white)
                            .setDuration(2000)
                            .setIcon(R.drawable.info, R.color.white)
                            .autoHide(true)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200)
                }
            }
        })
        constraintLayout = findViewById(R.id.cons)
        constraintLayout.setOnTouchListener(object : OnTouchListener {
            public override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                val inputMethodManager: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0)
                phone.clearFocus()
                return false
            }
        })
        phone.setOnFocusChangeListener(object : OnFocusChangeListener {
            public override fun onFocusChange(v: View, hasFocus: Boolean) {
                if (hasFocus) {
                    phone.setHint("")
                    phone.setText("+91 ")
                    Selection.setSelection(phone.getText(), phone.getText()!!.length)
                } else phone.setHint("Enter Phone Number")
            }
        })
        phone.addTextChangedListener(object : TextWatcher {
            public override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            public override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                                  after: Int) {
            }

            public override fun afterTextChanged(s: Editable) {
                if (!s.toString().startsWith("+91 ")) {
                    phone.setText("+91 ")
                    Selection.setSelection(phone.getText(), phone.getText()!!.length)
                }
            }
        })
    }

    public override fun onBackPressed() {
        super.onBackPressed()
        CustomIntent.customType(this@NewLogin, "fadein-to-fadeout")
    }
}