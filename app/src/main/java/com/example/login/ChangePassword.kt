package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.irozon.sneaker.Sneaker;
import com.valdesekamdem.library.mdtoast.MDToast;

import static maes.tech.intentanim.CustomIntent.customType;

public class ChangePassword extends AppCompatActivity {

    TextInputEditText oldpassword, newpassword, confirmpassword;
    TextView changepassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldpassword = findViewById(R.id.oldpassword);
        newpassword = findViewById(R.id.newpassword);
        confirmpassword = findViewById(R.id.repeatpassword);
        changepassword = findViewById(R.id.changepassword);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (oldpassword.getText().toString().equals("")) {
                            Sneaker.with(ChangePassword.this)
                                    .setTitle("Enter your old password", R.color.white)
                                    .setMessage("It cant be empty", R.color.white)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .setDuration(2000)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200);
                        }
                        else if (!oldpassword.getText().toString().equals(snapshot.child("password").getValue(String.class))) {
                            Sneaker.with(ChangePassword.this)
                                    .setTitle("Wrong old password", R.color.white)
                                    .setMessage("Old password does not match", R.color.white)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .setDuration(2000)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200);
                        }else if (newpassword.getText().toString().equals("")) {
                            Sneaker.with(ChangePassword.this)
                                    .setTitle("Enter your new password", R.color.white)
                                    .setMessage("It cant be empty", R.color.white)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .setDuration(2000)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200);
                        }
                        else if (newpassword.getText().toString().length() < 8) {
                            Sneaker.with(ChangePassword.this)
                                    .setTitle("Short Password", R.color.white)
                                    .setMessage("New Password should be minimum of 8 length", R.color.white)
                                    .setIcon(R.drawable.info,R.color.white)
                                    .setDuration(2000)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200);
                        }else if (confirmpassword.getText().toString().equals("")) {
                            Sneaker.with(ChangePassword.this)
                                    .setTitle("Confirm password", R.color.white)
                                    .setMessage("It cant be empty", R.color.white)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .setDuration(2000)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200);
                        }
                        else if (!confirmpassword.getText().toString().equals(newpassword.getText().toString())) {
                            Sneaker.with(ChangePassword.this)
                                    .setTitle("Passwords Do Not Match", R.color.white)
                                    .setMessage("Try Again Please!", R.color.white)
                                    .setDuration(2000)
                                    .setIcon(R.drawable.info,R.color.white)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200);
                        } else {
                            FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("password").setValue(newpassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        MDToast.makeText(getApplicationContext(),"Password Changed Successfully",MDToast.TYPE_SUCCESS,MDToast.LENGTH_SHORT).show();
                                        finish();
                                        customType(ChangePassword.this, "right-to-left");
                                    }
                                    else {
                                        Sneaker.with(ChangePassword.this)
                                                .setTitle("Error Changing Password", R.color.white)
                                                .setMessage("Try Again Please!", R.color.white)
                                                .setDuration(2000)
                                                .setIcon(R.drawable.info,R.color.white)
                                                .autoHide(true)
                                                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                .setCornerRadius(10, 0)
                                                .sneak(R.color.teal_200);
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(ChangePassword.this, "right-to-left");

    }
}