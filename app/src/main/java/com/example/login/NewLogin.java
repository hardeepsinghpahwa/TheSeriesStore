package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.irozon.sneaker.Sneaker;
import com.valdesekamdem.library.mdtoast.MDToast;

import static maes.tech.intentanim.CustomIntent.customType;

public class NewLogin extends AppCompatActivity {


    TextView sendotp;
    TextInputEditText phone;
    TextView getverificationcode;
    ConstraintLayout constraintLayout;
    int i=0;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_login);

        phone=findViewById(R.id.phone);
        sendotp=findViewById(R.id.proceed);
        progressBar=findViewById(R.id.progressbar2);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (phone.getText().length() == 14) {
                    progressBar.setVisibility(View.VISIBLE);

                    FirebaseDatabase.getInstance().getReference().child("Profiles").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            i=0;
                            for(DataSnapshot dataSnapshot:snapshot.getChildren())
                            {
                                if(dataSnapshot.child("phone").getValue(String.class).equals(phone.getText().toString().substring(4)))
                                {
                                    i++;
                                }
                            }

                            if(i>0)
                            {
                                progressBar.setVisibility(View.GONE);

                                Sneaker.with(NewLogin.this)
                                        .setTitle("Phone Number Already Registered", R.color.white)
                                        .setMessage("Please Sign In Instead", R.color.white)
                                        .setDuration(2000)
                                        .setIcon(R.drawable.info,R.color.white)
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

                            }else {
                                progressBar.setVisibility(View.GONE);

                                Intent intent=new Intent(NewLogin.this,VerifyOtp.class);
                                intent.putExtra("phone",phone.getText().toString().substring(4));
                                startActivity(intent);
                                customType(NewLogin.this,"left-to-right");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                } else {
                    //MDToast.makeText(NewLogin.this, "Enter a valid phone number", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                    Sneaker.with(NewLogin.this)
                            .setTitle("Invalid Phone Number", R.color.white)
                            .setMessage("Enter a valid phone number!", R.color.white)
                            .setDuration(2000)
                            .setIcon(R.drawable.info,R.color.white)
                            .autoHide(true)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200);
                }

            }
        });

        constraintLayout = findViewById(R.id.cons);

        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                phone.clearFocus();
                return false;

            }
        });
        phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    phone.setHint("");
                    phone.setText("+91 ");
                    Selection.setSelection(phone.getText(), phone.getText().length());
                } else
                    phone.setHint("Enter Phone Number");
            }
        });

        phone.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith("+91 ")) {
                    phone.setText("+91 ");
                    Selection.setSelection(phone.getText(), phone.getText().length());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(NewLogin.this,"fadein-to-fadeout");
    }
}