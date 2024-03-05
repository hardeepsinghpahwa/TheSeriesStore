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
import androidx.databinding.DataBindingUtil
import com.example.login.databinding.ActivityMainBinding
import com.example.login.databinding.ActivityNewLoginBinding

class NewLogin : AppCompatActivity() {
    var i: Int = 0
    lateinit var binding: ActivityNewLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_login)
        binding.executePendingBindings()
        findViewById<View>(R.id.back).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                onBackPressed()
            }
        })
        binding.proceed.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                if (binding.phone.text!!.length == 14) {
                    binding.progressbar2.visibility = View.VISIBLE
                    FirebaseDatabase.getInstance().reference.child("Profiles").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            i = 0
                            for (dataSnapshot: DataSnapshot in snapshot.children) {
                                if ((dataSnapshot.child("phone").getValue(String::class.java) == binding.phone.text.toString().substring(4))) {
                                    i++
                                }
                            }
                            if (i > 0) {
                                binding.progressbar2.visibility = View.GONE
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
                                binding.progressbar2.visibility = View.GONE
                                val intent: Intent = Intent(this@NewLogin, VerifyOtp::class.java)
                                intent.putExtra("phone", binding.phone.text.toString().substring(4))
                                startActivity(intent)
                                CustomIntent.customType(this@NewLogin, "left-to-right")
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
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
        binding.cons.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                val inputMethodManager: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                binding.phone.clearFocus()
                return false
            }
        })
        binding.phone.onFocusChangeListener = object : OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                if (hasFocus) {
                    binding.phone.hint = ""
                    binding.phone.setText("+91 ")
                    Selection.setSelection(binding.phone.text, binding.phone.text!!.length)
                } else binding.phone.hint = "Enter Phone Number"
            }
        }
        binding.phone.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                                  after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (!s.toString().startsWith("+91 ")) {
                    binding.phone.setText("+91 ")
                    Selection.setSelection(binding.phone.text, binding.phone.text!!.length)
                }
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        CustomIntent.customType(this@NewLogin, "fadein-to-fadeout")
    }
}