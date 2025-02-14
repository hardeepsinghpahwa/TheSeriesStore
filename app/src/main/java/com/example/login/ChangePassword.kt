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
import maes.tech.intentanim.CustomIntent
import com.google.android.material.textfield.TextInputEditText
import com.valdesekamdem.library.mdtoast.MDToast
import android.view.*
import androidx.databinding.DataBindingUtil
import com.example.login.databinding.ActivityChangePasswordBinding
import com.google.android.gms.tasks.Task

class ChangePassword constructor() : AppCompatActivity() {

    lateinit var binding:ActivityChangePasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)
        binding.executePendingBindings()

        binding.back.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                onBackPressedDispatcher.onBackPressed()
            }
        })
        binding.changepassword.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(object : ValueEventListener {
                    public override fun onDataChange(snapshot: DataSnapshot) {
                        if ((binding.oldpassword.getText().toString() == "")) {
                            Sneaker.with(this@ChangePassword)
                                    .setTitle("Enter your old password", R.color.white)
                                    .setMessage("It cant be empty", R.color.white)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .setDuration(2000)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200)
                        } else if (!(binding.oldpassword.getText().toString() == snapshot.child("password").getValue(String::class.java))) {
                            Sneaker.with(this@ChangePassword)
                                    .setTitle("Wrong old password", R.color.white)
                                    .setMessage("Old password does not match", R.color.white)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .setDuration(2000)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200)
                        } else if ((binding.newpassword.getText().toString() == "")) {
                            Sneaker.with(this@ChangePassword)
                                    .setTitle("Enter your new password", R.color.white)
                                    .setMessage("It cant be empty", R.color.white)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .setDuration(2000)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200)
                        } else if (binding.newpassword.getText().toString().length < 8) {
                            Sneaker.with(this@ChangePassword)
                                    .setTitle("Short Password", R.color.white)
                                    .setMessage("New Password should be minimum of 8 length", R.color.white)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .setDuration(2000)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200)
                        } else if ((binding.repeatpassword.getText().toString() == "")) {
                            Sneaker.with(this@ChangePassword)
                                    .setTitle("Confirm password", R.color.white)
                                    .setMessage("It cant be empty", R.color.white)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .setDuration(2000)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200)
                        } else if (!(binding.repeatpassword.getText().toString() == binding.newpassword.getText().toString())) {
                            Sneaker.with(this@ChangePassword)
                                    .setTitle("Passwords Do Not Match", R.color.white)
                                    .setMessage("Try Again Please!", R.color.white)
                                    .setDuration(2000)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200)
                        } else {
                            FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("password").setValue(binding.repeatpassword.getText().toString()).addOnCompleteListener(object : OnCompleteListener<Void?> {
                                public override fun onComplete(task: Task<Void?>) {
                                    if (task.isSuccessful()) {
                                        MDToast.makeText(getApplicationContext(), "Password Changed Successfully", MDToast.TYPE_SUCCESS, MDToast.LENGTH_SHORT).show()
                                        finish()
                                        CustomIntent.customType(this@ChangePassword, "right-to-left")
                                    } else {
                                        Sneaker.with(this@ChangePassword)
                                                .setTitle("Error Changing Password", R.color.white)
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
                        }
                    }

                    public override fun onCancelled(error: DatabaseError) {}
                })
            }
        })
    }

    public override fun onBackPressed() {
        super.onBackPressed()
        CustomIntent.customType(this@ChangePassword, "right-to-left")
    }
}