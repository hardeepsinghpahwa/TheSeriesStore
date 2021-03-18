package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.irozon.sneaker.Sneaker;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.regex.Pattern;

import static maes.tech.intentanim.CustomIntent.customType;

public class Login extends AppCompatActivity {

    TextInputEditText phoneemail, password;
    TextView login;
    int i = 0;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.login);
        phoneemail = findViewById(R.id.phoneoremail);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressbar2);


        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = 0;
                if ((!isValidMobile(phoneemail.getText().toString()) && !isValidMail(phoneemail.getText().toString()))) {
                    Sneaker.with(Login.this)
                            .setTitle("Invalid Mobile/Email Format", R.color.white)
                            .setMessage("Please Check The Mobile/Email entered", R.color.white)
                            .setDuration(2000)
                            .setIcon(R.drawable.info, R.color.white)
                            .autoHide(true)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200);
                } else if (password.getText().toString().length() < 8) {
                    Sneaker.with(Login.this)
                            .setTitle("Invalid Password", R.color.white)
                            .setMessage("Password is greater than 8 length", R.color.white)
                            .setDuration(2000)
                            .setIcon(R.drawable.info,R.color.white)
                            .autoHide(true)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200);

                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    if (isValidMobile(phoneemail.getText().toString())) {
                        FirebaseDatabase.getInstance().getReference().child("Profiles").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    if (dataSnapshot.child("phone").getValue(String.class).equals(phoneemail.getText().toString())) {

                                        FirebaseAuth.getInstance().signInWithEmailAndPassword(dataSnapshot.child("email").getValue(String.class), password.getText().toString())
                                                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            // Sign in success, update UI with the signed-in user's information
                                                            Log.d("TAG", "signInWithEmail:success");
                                                            progressBar.setVisibility(View.GONE);

                                                            Intent intent = new Intent(Login.this, Home.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(intent);
                                                            customType(Login.this,"left-to-right");
                                                            MDToast.makeText(getApplicationContext(), "Logged In Successfully", MDToast.TYPE_SUCCESS, MDToast.LENGTH_SHORT).show();

                                                        } else {
                                                            // If sign in fails, display a message to the user.
                                                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                                                            progressBar.setVisibility(View.GONE);

                                                            Sneaker.with(Login.this)
                                                                    .setTitle("Email/Mobile And Password Mismatch", R.color.white)
                                                                    .setMessage("Please Check Your Details Again", R.color.white)
                                                                    .setIcon(R.drawable.info,R.color.white)
                                                                    .setDuration(2000)
                                                                    .autoHide(true)
                                                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                                    .setCornerRadius(10, 0)
                                                                    .sneakWarning();

                                                        }

                                                        // ...
                                                    }
                                                });
                                        i = 1;
                                        break;
                                    }
                                }

                                if (i == 0) {
                                    Sneaker.with(Login.this)
                                            .setTitle("No Existing Account with this phone", R.color.white)
                                            .setMessage("Instead Sign Up Please", R.color.white)
                                            .setDuration(2000)
                                            .setIcon(R.drawable.info,R.color.white)
                                            .autoHide(true)
                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                            .setCornerRadius(10, 0)
                                            .sneakWarning();
                                    progressBar.setVisibility(View.GONE);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    } else {
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(phoneemail.getText().toString(), password.getText().toString())
                                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d("TAG", "signInWithEmail:success");
                                            progressBar.setVisibility(View.GONE);

                                            Intent intent = new Intent(Login.this, Home.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            customType(Login.this,"left-to-right");

                                            MDToast.makeText(getApplicationContext(), "Logged In Successfully", MDToast.TYPE_SUCCESS, MDToast.LENGTH_SHORT).show();

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                                            progressBar.setVisibility(View.GONE);

                                            Sneaker.with(Login.this)
                                                    .setTitle("Email/Mobile And Password Mismatch", R.color.white)
                                                    .setMessage("Please Check Your Details Again", R.color.white)
                                                    .setIcon(R.drawable.info,R.color.white)
                                                    .setDuration(2000)
                                                    .autoHide(true)
                                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                    .setCornerRadius(10, 0)
                                                    .sneakWarning();

                                        }

                                        // ...
                                    }
                                });

                    }

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(Login.this, "fadein-to-fadeout");
    }

    private boolean isValidMobile(String phone) {
        return Patterns.PHONE.matcher(phone).matches() && phone.length() == 10;

    }

    private boolean isValidMail(String email) {

        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        return Pattern.compile(EMAIL_STRING).matcher(email).matches();

    }
}