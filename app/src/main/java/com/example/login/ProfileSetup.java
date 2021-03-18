package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.irozon.sneaker.Sneaker;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

import static maes.tech.intentanim.CustomIntent.customType;

public class ProfileSetup extends AppCompatActivity {


    TextInputEditText name, email, phone, password, confirmpassword;
    TextView createaccount, login;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        confirmpassword = findViewById(R.id.repeatpassword);
        createaccount = findViewById(R.id.createaccount);
        login = findViewById(R.id.login);
        phone.setKeyListener(null);

        phone.setText(getIntent().getStringExtra("phone"));

        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().equals("")) {
                    Sneaker.with(ProfileSetup.this)
                            .setTitle("Enter you name", R.color.white)
                            .setMessage("Name cant be empty", R.color.white)
                            .setIcon(R.drawable.info,R.color.white)
                            .setDuration(2000)
                            .autoHide(true)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200);
                } else if (email.getText().toString().equals("")) {
                    Sneaker.with(ProfileSetup.this)
                            .setTitle("Enter you email", R.color.white)
                            .setMessage("Email cant be empty", R.color.white)
                            .setIcon(R.drawable.info,R.color.white)
                            .setDuration(2000)
                            .autoHide(true)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200);
                } else if (!isValidEmail(email.getText().toString())){
                    Sneaker.with(ProfileSetup.this)
                            .setTitle("Invalid Email", R.color.white)
                            .setMessage("Enter a valid email", R.color.white)
                            .setDuration(2000)
                            .autoHide(true)
                            .setIcon(R.drawable.info,R.color.white)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200);
                }else if (password.getText().toString().equals("")) {
                    Sneaker.with(ProfileSetup.this)
                            .setTitle("Enter you password", R.color.white)
                            .setMessage("Password cant be empty", R.color.white)
                            .setDuration(2000)
                            .setIcon(R.drawable.info,R.color.white)
                            .autoHide(true)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200);
                }  else if (password.getText().toString().length() < 8) {
                    Sneaker.with(ProfileSetup.this)
                            .setTitle("Short Password", R.color.white)
                            .setMessage("Password must be minimum of 8 length", R.color.white)
                            .setIcon(R.drawable.info,R.color.white)
                            .setDuration(2000)
                            .autoHide(true)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200);
                } else if (!containsdigit(password.getText().toString())) {
                    Sneaker.with(ProfileSetup.this)
                            .setTitle("Invalid Password", R.color.white)
                            .setMessage("Password must contain a number", R.color.white)
                            .setDuration(2000)
                            .setIcon(R.drawable.info,R.color.white)
                            .autoHide(true)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200);
                } else if (!confirmpassword.getText().toString().equals(password.getText().toString())) {
                    Sneaker.with(ProfileSetup.this)
                            .setTitle("Passwords Do Not Match", R.color.white)
                            .setMessage("Try Again Please!", R.color.white)
                            .setDuration(2000)
                            .setIcon(R.drawable.info,R.color.white)
                            .autoHide(true)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200);
                } else {

                    FirebaseDatabase.getInstance().getReference().child("Profiles").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            i = 0;
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (Objects.equals(dataSnapshot.child("email").getValue(String.class), email.getText().toString())) {
                                    i++;
                                }
                            }

                            if (i > 0) {

                                Sneaker.with(ProfileSetup.this)
                                        .setTitle("Email Already Registered", R.color.white)
                                        .setMessage("Please Use A Different Email", R.color.white)
                                        .setIcon(R.drawable.info,R.color.white)
                                        .setDuration(2000)
                                        .autoHide(true)
                                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                        .setCornerRadius(10, 0)
                                        .sneak(R.color.teal_200);
                                /*final Handler handler = new Handler(Looper.getMainLooper());
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 2000);*/

                            } else {

                                AuthCredential credential = EmailAuthProvider.getCredential(email.getText().toString(), password.getText().toString());

                                Map map = new HashMap();
                                map.put("name", name.getText().toString());
                                map.put("email", email.getText().toString());
                                map.put("phone", phone.getText().toString());
                                map.put("password", password.getText().toString());

                                final AlertDialog alertDialog = new SpotsDialog.Builder()
                                        .setCancelable(false)
                                        .setContext(ProfileSetup.this)
                                        .setTheme(R.style.ProgressDialog)
                                        .setMessage("Saving Your Details")
                                        .build();

                                alertDialog.show();

                                FirebaseAuth.getInstance().getCurrentUser().linkWithCredential(credential)
                                        .addOnCompleteListener(ProfileSetup.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {
                                                            if (task.isSuccessful()) {
                                                                alertDialog.dismiss();
                                                                MDToast.makeText(getApplicationContext(), "Account Created Successfully", MDToast.TYPE_SUCCESS, MDToast.LENGTH_SHORT).show();

                                                                startActivity(new Intent(ProfileSetup.this, Home.class));
                                                                customType(ProfileSetup.this, "fadein-to-fadeout");
                                                                finish();

                                                            } else {
                                                                alertDialog.dismiss();
                                                                Sneaker.with(ProfileSetup.this)
                                                                        .setTitle("Error Saving Details", R.color.white)
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

                                                } else {
                                                    Log.w("TAG", "linkWithCredential:failure", task.getException());
                                                    alertDialog.dismiss();
                                                    Sneaker.with(ProfileSetup.this)
                                                            .setTitle("Error Saving Details", R.color.white)
                                                            .setIcon(R.drawable.info,R.color.white)
                                                            .setMessage("Try Again Please!", R.color.white)
                                                            .setDuration(2000)
                                                            .autoHide(true)
                                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                            .setCornerRadius(10, 0)
                                                            .sneakError();
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

            }
        });


    }

    boolean containsdigit(String input) {
        boolean containsDigit = false;
        if (input != null && !input.isEmpty()) {
            for (char c : input.toCharArray()) {
                containsDigit = Character.isDigit(c);
                if (containsDigit) {
                    break;
                }
            }
        }
        return containsDigit;
    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}