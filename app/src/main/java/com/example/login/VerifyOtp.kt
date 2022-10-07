package com.example.login

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import android.os.Bundle
import android.widget.EditText
import androidx.core.content.ContextCompat
import android.view.View.OnTouchListener
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
import androidx.cardview.widget.CardView
import com.daimajia.androidanimations.library.YoYo
import com.daimajia.androidanimations.library.Techniques
import android.widget.ProgressBar
import com.google.firebase.auth.AuthResult
import android.app.AlertDialog
import android.graphics.Color
import android.text.TextWatcher
import android.text.Editable
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.FirebaseException
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.android.gms.tasks.Task
import java.util.concurrent.TimeUnit

class VerifyOtp : AppCompatActivity() {
    lateinit var code1: EditText
    lateinit var code2: EditText
    lateinit var code3: EditText
    lateinit var code4: EditText
    lateinit var code5: EditText
    lateinit var code6: EditText
    lateinit var sendingverifcationtext: TextView
    lateinit var resend: TextView
    lateinit var codesentto: TextView
    lateinit var code: String
    lateinit var phone: String
    lateinit var p: String
    lateinit var v_id: String
    var counter: Int = 30
    var a: Int = 0
    lateinit var lottieAnimationView: LottieAnimationView
    lateinit var proceed: TextView
    lateinit var didnotget: TextView
    lateinit var constraintLayout: ConstraintLayout
    lateinit var back: CardView
    lateinit var proceedcard: CardView
    lateinit var progressBar: ProgressBar
    lateinit var progressBar2: ProgressBar
    lateinit var resendotp: ForceResendingToken
    lateinit var mCallBacks: OnVerificationStateChangedCallbacks
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)
        phone = getIntent().getStringExtra("phone")!!
        p = phone!!.replace("\\s".toRegex(), "")
        Log.i("phone", p!!)
        findViewById<View>(R.id.imageView4).bringToFront()
        lottieAnimationView = findViewById(R.id.lottie)
        code1 = findViewById(R.id.code1)
        code2 = findViewById(R.id.code2)
        code3 = findViewById(R.id.code3)
        code4 = findViewById(R.id.code4)
        code5 = findViewById(R.id.code5)
        code6 = findViewById(R.id.code6)
        proceed = findViewById(R.id.proceed)
        codesentto = findViewById(R.id.sendingcodetext)
        resend = findViewById(R.id.sendcodeagain)
        didnotget = findViewById(R.id.didnotgettext)
        proceedcard = findViewById(R.id.proceedcard)
        sendingverifcationtext = findViewById(R.id.sendingcodetext)
        constraintLayout = findViewById(R.id.cons2)
        progressBar = findViewById(R.id.progressbar)
        back = findViewById(R.id.back)
        progressBar2 = findViewById(R.id.progressbar2)
        constraintLayout.setOnTouchListener(object : OnTouchListener {
            public override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                val inputMethodManager: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0)
                return false
            }
        })
        proceed.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                progressBar2.setVisibility(View.VISIBLE)
                proceed.setEnabled(false)
                FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(object : ValueEventListener {
                    public override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.child("name").exists()) {
                                val intent: Intent = Intent(this@VerifyOtp, Home::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                                CustomIntent.customType(this@VerifyOtp, "fadein-to-fadeout")
                            } else if (snapshot.child("phone").exists()) {
                                val intent: Intent = (Intent(this@VerifyOtp, ProfileSetup::class.java))
                                intent.putExtra("phone", snapshot.child("phone").getValue(String::class.java))
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                                CustomIntent.customType(this@VerifyOtp, "left-to-right")
                            }
                        } else {
                            val intent: Intent = (Intent(this@VerifyOtp, ProfileSetup::class.java))
                            intent.putExtra("phone", snapshot.child("phone").getValue(String::class.java))
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                            CustomIntent.customType(this@VerifyOtp, "left-to-right")
                        }
                    }

                    public override fun onCancelled(error: DatabaseError) {}
                })
            }
        })
        mCallBacks = object : OnVerificationStateChangedCallbacks() {
            public override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                code = phoneAuthCredential.getSmsCode()
                if (code != null) {
                    code1.setText(code!!.get(0).toString())
                    code2.setText(code!!.get(1).toString())
                    code3.setText(code!!.get(2).toString())
                    code4.setText(code!!.get(3).toString())
                    code5.setText(code!!.get(4).toString())
                    code6.setText(code!!.get(5).toString())
                    code6.clearFocus()
                }
            }

            public override fun onVerificationFailed(e: FirebaseException) {
                Sneaker.with(this@VerifyOtp)
                        .setTitle("Some Error Occurred", R.color.white)
                        .setMessage("Please Try Again!", R.color.white)
                        .setDuration(2000)
                        .setIcon(R.drawable.info, R.color.white)
                        .autoHide(true)
                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setCornerRadius(10, 0)
                        .sneak(R.color.teal_200)
                onBackPressed()
            }

            public override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                Log.i("token", forceResendingToken.toString())
                v_id = s
                resendotp = forceResendingToken
                sendingverifcationtext.setText("Verification Code Sent")
                codesentto.setText("Code sent to " + phone)
                YoYo.with(Techniques.FadeIn)
                        .duration(800)
                        .playOn(sendingverifcationtext)
                progressBar.setVisibility(View.GONE)
                code1.setEnabled(true)
                code2.setEnabled(true)
                code3.setEnabled(true)
                code4.setEnabled(true)
                code5.setEnabled(true)
                code6.setEnabled(true)
                if (a == 0) {
                    object : CountDownTimer(30000, 1000) {
                        public override fun onTick(millisUntilFinished: Long) {
                            resend.setText("Send Again In " + counter)
                            counter--
                        }

                        public override fun onFinish() {
                            resend.setText("Send Again")
                            resend.setOnClickListener(object : View.OnClickListener {
                                public override fun onClick(v: View) {
                                    a = 1
                                    resend.setText("Code Sent")
                                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                            "+91" + p,  // Phone number to verify
                                            1,  // Timeout duration
                                            TimeUnit.MINUTES,  // Unit of timeout
                                            this@VerifyOtp,  // Activity (for callback binding)
                                            mCallBacks,  // OnVerificationStateChangedCallbacks
                                            resendotp)
                                }
                            })
                        }
                    }.start()
                }
            }
        }
        code1.addTextChangedListener(object : TextWatcher {
            public override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            public override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().length == 1) {
                    code1.clearFocus()
                    code2.requestFocus()
                }
                if (!(code1.getText().toString() == "") && !(code2.getText().toString() == "") && !(code3.getText().toString() == "") && !(code4.getText().toString() == "") && !(code5.getText().toString() == "") && !(code6.getText().toString() == "")) {
                    signInWithPhoneAuthCredential(v_id, code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString() + code5.getText().toString() + code6.getText().toString())
                }
            }

            public override fun afterTextChanged(s: Editable) {}
        })
        code2.addTextChangedListener(object : TextWatcher {
            public override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            public override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().length == 1) {
                    code2.clearFocus()
                    code3.requestFocus()
                } else {
                    code2.setOnKeyListener(object : View.OnKeyListener {
                        public override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                            if (keyCode == KeyEvent.KEYCODE_DEL && s.toString().length == 0) {
                                code2.clearFocus()
                                code1.requestFocus()
                            }
                            return false
                        }
                    })
                }
                if (!(code1.getText().toString() == "") && !(code2.getText().toString() == "") && !(code3.getText().toString() == "") && !(code4.getText().toString() == "") && !(code5.getText().toString() == "") && !(code6.getText().toString() == "")) {
                    signInWithPhoneAuthCredential(v_id, code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString() + code5.getText().toString() + code6.getText().toString())
                }
            }

            public override fun afterTextChanged(s: Editable) {}
        })
        code3.addTextChangedListener(object : TextWatcher {
            public override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            public override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().length == 1) {
                    code3.clearFocus()
                    code4.requestFocus()
                } else {
                    code3.setOnKeyListener(object : View.OnKeyListener {
                        public override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                            if (keyCode == KeyEvent.KEYCODE_DEL && s.toString().length == 0) {
                                code3.clearFocus()
                                code2.requestFocus()
                            }
                            return false
                        }
                    })
                }
                if (!(code1.getText().toString() == "") && !(code2.getText().toString() == "") && !(code3.getText().toString() == "") && !(code4.getText().toString() == "") && !(code5.getText().toString() == "") && !(code6.getText().toString() == "")) {
                    signInWithPhoneAuthCredential(v_id, code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString() + code5.getText().toString() + code6.getText().toString())
                }
            }

            public override fun afterTextChanged(s: Editable) {}
        })
        code4.addTextChangedListener(object : TextWatcher {
            public override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            public override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().length == 1) {
                    code4.clearFocus()
                    code5.requestFocus()
                } else {
                    code4.setOnKeyListener(object : View.OnKeyListener {
                        public override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                            if (keyCode == KeyEvent.KEYCODE_DEL && s.toString().length == 0) {
                                code4.clearFocus()
                                code3.requestFocus()
                            }
                            return false
                        }
                    })
                }
                if (!(code1.getText().toString() == "") && !(code2.getText().toString() == "") && !(code3.getText().toString() == "") && !(code4.getText().toString() == "") && !(code5.getText().toString() == "") && !(code6.getText().toString() == "")) {
                    signInWithPhoneAuthCredential(v_id, code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString() + code5.getText().toString() + code6.getText().toString())
                }
            }

            public override fun afterTextChanged(s: Editable) {}
        })
        code5.addTextChangedListener(object : TextWatcher {
            public override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            public override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().length == 1) {
                    code5.clearFocus()
                    code6.requestFocus()
                } else {
                    code5.setOnKeyListener(object : View.OnKeyListener {
                        public override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                            if (keyCode == KeyEvent.KEYCODE_DEL && s.toString().length == 0) {
                                code5.clearFocus()
                                code4.requestFocus()
                            }
                            return false
                        }
                    })
                }
                if (!(code1.getText().toString() == "") && !(code2.getText().toString() == "") && !(code3.getText().toString() == "") && !(code4.getText().toString() == "") && !(code5.getText().toString() == "") && !(code6.getText().toString() == "")) {
                    signInWithPhoneAuthCredential(v_id, code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString() + code5.getText().toString() + code6.getText().toString())
                }
            }

            public override fun afterTextChanged(s: Editable) {}
        })
        code6.addTextChangedListener(object : TextWatcher {
            public override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            public override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!(code1.getText().toString() == "") && !(code2.getText().toString() == "") && !(code3.getText().toString() == "") && !(code4.getText().toString() == "") && !(code5.getText().toString() == "") && !(code6.getText().toString() == "")) {
                    signInWithPhoneAuthCredential(v_id, code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString() + code5.getText().toString() + code6.getText().toString())
                }
                if ((code6.getText().toString() == "")) {
                    code6.setOnKeyListener(object : View.OnKeyListener {
                        public override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                            if (keyCode == KeyEvent.KEYCODE_DEL && s.toString().length == 0) {
                                code6.clearFocus()
                                code5.requestFocus()
                            }
                            return false
                        }
                    })
                }
            }

            public override fun afterTextChanged(s: Editable) {}
        })
        val options: PhoneAuthOptions = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber("+91" + p) // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this) // Activity (for callback binding)
                .setCallbacks(mCallBacks) // OnVerificationStateChangedCallbacks
                .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(id: String?, c: String) {
        val alertDialog: AlertDialog = SpotsDialog.Builder()
                .setCancelable(false)
                .setContext(this@VerifyOtp)
                .setTheme(R.style.ProgressDialog)
                .setMessage("Verifying Phone")
                .build()
        alertDialog.show()
        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(id, c)
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, object : OnCompleteListener<AuthResult> {
                    public override fun onComplete(task: Task<AuthResult>) {
                        alertDialog.dismiss()
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success")
                            val user: FirebaseUser = task.getResult()!!.getUser()
                            FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(object : ValueEventListener {
                                public override fun onDataChange(snapshot: DataSnapshot) {
                                    snapshot.getRef().child("phone").setValue(p)
                                }

                                public override fun onCancelled(error: DatabaseError) {}
                            })
                            codesentto!!.setText("OTP VERIFIED")
                            codesentto!!.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.verified, 0, 0, 0)
                            codesentto!!.setCompoundDrawablePadding(10)
                            codesentto!!.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey))
                            didnotget!!.setVisibility(View.GONE)
                            lottieAnimationView!!.setVisibility(View.VISIBLE)
                            lottieAnimationView!!.playAnimation()
                            resend!!.setVisibility(View.GONE)
                            proceedcard!!.setVisibility(View.VISIBLE)
                            YoYo.with(Techniques.SlideInUp)
                                    .duration(300)
                                    .playOn(proceedcard)
                            code1!!.setKeyListener(null)
                            code2!!.setKeyListener(null)
                            code3!!.setKeyListener(null)
                            code4!!.setKeyListener(null)
                            code5!!.setKeyListener(null)
                            code6!!.setKeyListener(null)
                            code1!!.clearFocus()
                            code2!!.clearFocus()
                            code3!!.clearFocus()
                            code4!!.clearFocus()
                            code5!!.clearFocus()
                            code6!!.clearFocus()
                            val inputMethodManager: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.hideSoftInputFromWindow(constraintLayout!!.getWindowToken(), 0)

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Sneaker.with(this@VerifyOtp)
                                    .setTitle("Invalid OTP Entered", R.color.white)
                                    .setMessage("Please Check Your OTP", R.color.white)
                                    .setDuration(2000)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200)
                            codesentto!!.setText("WRONG OTP")
                            codesentto!!.setTextColor(Color.RED)
                            code1!!.setText("")
                            code2!!.setText("")
                            code3!!.setText("")
                            code4!!.setText("")
                            code5!!.setText("")
                            code6!!.setText("")
                            code6!!.clearFocus()
                            val inputMethodManager: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.hideSoftInputFromWindow(constraintLayout!!.getWindowToken(), 0)
                            if (task.getException() is FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                })
    }

    public override fun onBackPressed() {
        super.onBackPressed()
        finish()
        CustomIntent.customType(this@VerifyOtp, "left-to-right")
    }
}