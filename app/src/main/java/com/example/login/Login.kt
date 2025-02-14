package com.example.login

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.os.Bundle
import com.irozon.sneaker.Sneaker
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
import com.google.firebase.auth.AuthResult
import android.util.Log
import android.util.Patterns
import android.view.*
import androidx.databinding.DataBindingUtil
import com.example.login.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.Task
import java.util.regex.Pattern

class Login : AppCompatActivity() {

    var i: Int = 0
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.executePendingBindings()

        findViewById<View>(R.id.back).setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                onBackPressedDispatcher.onBackPressed()
            }
        })
        binding.login.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                i = 0
                if ((!isValidMobile(binding.phoneoremail.getText().toString()) && !isValidMail(binding.phoneoremail.getText().toString()))) {
                    Sneaker.with(this@Login)
                            .setTitle("Invalid Mobile/Email Format", R.color.white)
                            .setMessage("Please Check The Mobile/Email entered", R.color.white)
                            .setDuration(2000)
                            .setIcon(R.drawable.info, R.color.white)
                            .autoHide(true)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200)
                } else if (binding.password.getText().toString().length < 8) {
                    Sneaker.with(this@Login)
                            .setTitle("Invalid Password", R.color.white)
                            .setMessage("Password is greater than 8 length", R.color.white)
                            .setDuration(2000)
                            .setIcon(R.drawable.info, R.color.white)
                            .autoHide(true)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200)
                } else {
                    binding.progressbar2.setVisibility(View.VISIBLE)
                    if (isValidMobile(binding.phoneoremail.getText().toString())) {
                        FirebaseDatabase.getInstance().getReference().child("Profiles").addListenerForSingleValueEvent(object : ValueEventListener {
                            public override fun onDataChange(snapshot: DataSnapshot) {
                                for (dataSnapshot: DataSnapshot in snapshot.getChildren()) {
                                    if ((dataSnapshot.child("phone").getValue(String::class.java) == binding.phoneoremail.getText().toString())) {
                                        FirebaseAuth.getInstance().signInWithEmailAndPassword(dataSnapshot.child("email").getValue(String::class.java), binding.password.getText().toString())
                                                .addOnCompleteListener(this@Login, object : OnCompleteListener<AuthResult?> {
                                                    public override fun onComplete(task: Task<AuthResult?>) {
                                                        if (task.isSuccessful()) {
                                                            // Sign in success, update UI with the signed-in user's information
                                                            Log.d("TAG", "signInWithEmail:success")
                                                            binding.progressbar2.setVisibility(View.GONE)
                                                            val intent: Intent = Intent(this@Login, Home::class.java)
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                            startActivity(intent)
                                                            CustomIntent.customType(this@Login, "left-to-right")
                                                            MDToast.makeText(getApplicationContext(), "Logged In Successfully", MDToast.TYPE_SUCCESS, MDToast.LENGTH_SHORT).show()
                                                        } else {
                                                            // If sign in fails, display a message to the user.
                                                            Log.w("TAG", "signInWithEmail:failure", task.getException())
                                                            binding.progressbar2.setVisibility(View.GONE)
                                                            Sneaker.with(this@Login)
                                                                    .setTitle("Email/Mobile And Password Mismatch", R.color.white)
                                                                    .setMessage("Please Check Your Details Again", R.color.white)
                                                                    .setIcon(R.drawable.info, R.color.white)
                                                                    .setDuration(2000)
                                                                    .autoHide(true)
                                                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                                    .setCornerRadius(10, 0)
                                                                    .sneakWarning()
                                                        }

                                                        // ...
                                                    }
                                                })
                                        i = 1
                                        break
                                    }
                                }
                                if (i == 0) {
                                    Sneaker.with(this@Login)
                                            .setTitle("No Existing Account with this phone", R.color.white)
                                            .setMessage("Instead Sign Up Please", R.color.white)
                                            .setDuration(2000)
                                            .setIcon(R.drawable.info, R.color.white)
                                            .autoHide(true)
                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                            .setCornerRadius(10, 0)
                                            .sneakWarning()
                                    binding.progressbar2.setVisibility(View.GONE)
                                }
                            }

                            public override fun onCancelled(error: DatabaseError) {}
                        })
                    } else {
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.phoneoremail.getText().toString(), binding.password.getText().toString())
                                .addOnCompleteListener(this@Login, object : OnCompleteListener<AuthResult?> {
                                    public override fun onComplete(task: Task<AuthResult?>) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d("TAG", "signInWithEmail:success")
                                            binding.progressbar2.setVisibility(View.GONE)
                                            val intent: Intent = Intent(this@Login, Home::class.java)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            startActivity(intent)
                                            CustomIntent.customType(this@Login, "left-to-right")
                                            MDToast.makeText(getApplicationContext(), "Logged In Successfully", MDToast.TYPE_SUCCESS, MDToast.LENGTH_SHORT).show()
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w("TAG", "signInWithEmail:failure", task.getException())
                                            binding.progressbar2.setVisibility(View.GONE)
                                            Sneaker.with(this@Login)
                                                    .setTitle("Email/Mobile And Password Mismatch", R.color.white)
                                                    .setMessage("Please Check Your Details Again", R.color.white)
                                                    .setIcon(R.drawable.info, R.color.white)
                                                    .setDuration(2000)
                                                    .autoHide(true)
                                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                    .setCornerRadius(10, 0)
                                                    .sneakWarning()
                                        }

                                        // ...
                                    }
                                })
                    }
                }
            }
        })
    }

    public override fun onBackPressed() {
        super.onBackPressed()
        CustomIntent.customType(this@Login, "fadein-to-fadeout")
    }

    private fun isValidMobile(phone: String): Boolean {
        return Patterns.PHONE.matcher(phone).matches() && phone.length == 10
    }

    private fun isValidMail(email: String): Boolean {
        val EMAIL_STRING: String = ("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
        return Pattern.compile(EMAIL_STRING).matcher(email).matches()
    }
}