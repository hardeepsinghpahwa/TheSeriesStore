package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.irozon.sneaker.Sneaker;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;

import static maes.tech.intentanim.CustomIntent.customType;

public class VerifyOtp extends AppCompatActivity {

    EditText code1, code2, code3, code4, code5, code6;
    TextView sendingverifcationtext, resend, codesentto;
    String code;
    String phone, p, v_id;
    int counter = 30, a = 0;
    LottieAnimationView lottieAnimationView;
    TextView proceed,didnotget;
    ConstraintLayout constraintLayout;
    CardView back,proceedcard;
    ProgressBar progressBar,progressBar2;
    PhoneAuthProvider.ForceResendingToken resendotp;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        phone = getIntent().getStringExtra("phone");
        p = phone.replaceAll("\\s", "");
        Log.i("phone",p);

        findViewById(R.id.imageView4).bringToFront();

        lottieAnimationView=findViewById(R.id.lottie);
        code1 = findViewById(R.id.code1);
        code2 = findViewById(R.id.code2);
        code3 = findViewById(R.id.code3);
        code4 = findViewById(R.id.code4);
        code5 = findViewById(R.id.code5);
        code6 = findViewById(R.id.code6);
        proceed=findViewById(R.id.proceed);
        codesentto = findViewById(R.id.sendingcodetext);
        resend = findViewById(R.id.sendcodeagain);
        didnotget=findViewById(R.id.didnotgettext);
        proceedcard=findViewById(R.id.proceedcard);
        sendingverifcationtext = findViewById(R.id.sendingcodetext);
        constraintLayout = findViewById(R.id.cons2);
        progressBar=findViewById(R.id.progressbar);
        back=findViewById(R.id.back);
        progressBar2=findViewById(R.id.progressbar2);

        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                return false;

            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar2.setVisibility(View.VISIBLE);
                proceed.setEnabled(false);
                FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            if(snapshot.child("name").exists())
                            {
                                Intent intent=new Intent(VerifyOtp.this,Home.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                customType(VerifyOtp.this,"fadein-to-fadeout");
                            }
                            else if(snapshot.child("phone").exists())
                            {
                                Intent intent=(new Intent(VerifyOtp.this,ProfileSetup.class));
                                intent.putExtra("phone",snapshot.child("phone").getValue(String.class));
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                customType(VerifyOtp.this,"left-to-right");
                            }
                        }
                        else {
                            Intent intent=(new Intent(VerifyOtp.this,ProfileSetup.class));
                            intent.putExtra("phone",snapshot.child("phone").getValue(String.class));
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            customType(VerifyOtp.this,"left-to-right");

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                code = phoneAuthCredential.getSmsCode();

                if (code != null) {
                    code1.setText(String.valueOf(code.charAt(0)));
                    code2.setText(String.valueOf(code.charAt(1)));
                    code3.setText(String.valueOf(code.charAt(2)));
                    code4.setText(String.valueOf(code.charAt(3)));
                    code5.setText(String.valueOf(code.charAt(4)));
                    code6.setText(String.valueOf(code.charAt(5)));

                    code6.clearFocus();
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Sneaker.with(VerifyOtp.this)
                        .setTitle("Some Error Occurred", R.color.white)
                        .setMessage("Please Try Again!", R.color.white)
                        .setDuration(2000)
                        .setIcon(R.drawable.info,R.color.white)
                        .autoHide(true)
                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setCornerRadius(10, 0)
                        .sneak(R.color.teal_200);
                onBackPressed();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull final PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.i("token", forceResendingToken.toString());

                v_id = s;
                resendotp = forceResendingToken;

                sendingverifcationtext.setText("Verification Code Sent");
                codesentto.setText("Code sent to " +phone);
                YoYo.with(Techniques.FadeIn)
                        .duration(800)
                        .playOn(sendingverifcationtext);
                progressBar.setVisibility(View.GONE);

                code1.setEnabled(true);
                code2.setEnabled(true);
                code3.setEnabled(true);
                code4.setEnabled(true);
                code5.setEnabled(true);
                code6.setEnabled(true);


                if (a == 0) {
                    new CountDownTimer(30000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            resend.setText("Send Again In " + counter);
                            counter--;
                        }

                        @Override
                        public void onFinish() {
                            resend.setText("Send Again");

                            resend.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    a = 1;
                                    resend.setText("Code Sent");
                                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                            "+91"+p,        // Phone number to verify
                                            1,               // Timeout duration
                                            TimeUnit.MINUTES,   // Unit of timeout
                                            VerifyOtp.this,               // Activity (for callback binding)
                                            mCallBacks,         // OnVerificationStateChangedCallbacks
                                            resendotp);
                                }
                            });
                        }
                    }.start();
                }
            }
        };

        code1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 1) {
                    code1.clearFocus();
                    code2.requestFocus();
                }
                if (!code1.getText().toString().equals("") && !code2.getText().toString().equals("") && !code3.getText().toString().equals("") && !code4.getText().toString().equals("") && !code5.getText().toString().equals("") && !code6.getText().toString().equals("")) {
                    signInWithPhoneAuthCredential(v_id, code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString() + code5.getText().toString() + code6.getText().toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 1) {
                    code2.clearFocus();
                    code3.requestFocus();
                } else {
                    code2.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                            if (keyCode == KeyEvent.KEYCODE_DEL && s.toString().length() == 0) {
                                code2.clearFocus();
                                code1.requestFocus();
                            }

                            return false;
                        }
                    });
                }
                if (!code1.getText().toString().equals("") && !code2.getText().toString().equals("") && !code3.getText().toString().equals("") && !code4.getText().toString().equals("") && !code5.getText().toString().equals("") && !code6.getText().toString().equals("")) {
                    signInWithPhoneAuthCredential(v_id, code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString() + code5.getText().toString() + code6.getText().toString());
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 1) {
                    code3.clearFocus();
                    code4.requestFocus();
                } else {
                    code3.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                            if (keyCode == KeyEvent.KEYCODE_DEL && s.toString().length() == 0) {
                                code3.clearFocus();
                                code2.requestFocus();
                            }
                            return false;
                        }
                    });
                }
                if (!code1.getText().toString().equals("") && !code2.getText().toString().equals("") && !code3.getText().toString().equals("") && !code4.getText().toString().equals("") && !code5.getText().toString().equals("") && !code6.getText().toString().equals("")) {
                    signInWithPhoneAuthCredential(v_id, code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString() + code5.getText().toString() + code6.getText().toString());
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 1) {
                    code4.clearFocus();
                    code5.requestFocus();
                } else {
                    code4.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                            if (keyCode == KeyEvent.KEYCODE_DEL && s.toString().length() == 0) {
                                code4.clearFocus();
                                code3.requestFocus();
                            }
                            return false;
                        }
                    });
                }
                if (!code1.getText().toString().equals("") && !code2.getText().toString().equals("") && !code3.getText().toString().equals("") && !code4.getText().toString().equals("") && !code5.getText().toString().equals("") && !code6.getText().toString().equals("")) {
                    signInWithPhoneAuthCredential(v_id, code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString() + code5.getText().toString() + code6.getText().toString());
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 1) {
                    code5.clearFocus();
                    code6.requestFocus();
                } else {
                    code5.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                            if (keyCode == KeyEvent.KEYCODE_DEL && s.toString().length() == 0) {
                                code5.clearFocus();
                                code4.requestFocus();
                            }
                            return false;
                        }
                    });
                }
                if (!code1.getText().toString().equals("") && !code2.getText().toString().equals("") && !code3.getText().toString().equals("") && !code4.getText().toString().equals("") && !code5.getText().toString().equals("") && !code6.getText().toString().equals("")) {
                    signInWithPhoneAuthCredential(v_id, code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString() + code5.getText().toString() + code6.getText().toString());
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (!code1.getText().toString().equals("") && !code2.getText().toString().equals("") && !code3.getText().toString().equals("") && !code4.getText().toString().equals("") && !code5.getText().toString().equals("") && !code6.getText().toString().equals("")) {
                    signInWithPhoneAuthCredential(v_id, code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString() + code5.getText().toString() + code6.getText().toString());
                }

                if (code6.getText().toString().equals("")) {
                    code6.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                            if (keyCode == KeyEvent.KEYCODE_DEL && s.toString().length() == 0) {
                                code6.clearFocus();
                                code5.requestFocus();
                            }
                            return false;
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                        .setPhoneNumber("+91"+p)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);


    }

    private void signInWithPhoneAuthCredential(String id, String c) {

        final AlertDialog alertDialog = new SpotsDialog.Builder()
                .setCancelable(false)
                .setContext(VerifyOtp.this)
                .setTheme(R.style.ProgressDialog)
                .setMessage("Verifying Phone")
                .build();

        alertDialog.show();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, c);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        alertDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();

                            FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    snapshot.getRef().child("phone").setValue(p);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                            codesentto.setText("OTP VERIFIED");
                            codesentto.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.verified,0,0,0);
                            codesentto.setCompoundDrawablePadding(10);
                            codesentto.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.darkgrey));
                            didnotget.setVisibility(View.GONE);
                            lottieAnimationView.setVisibility(View.VISIBLE);
                            lottieAnimationView.playAnimation();
                            resend.setVisibility(View.GONE);
                            proceedcard.setVisibility(View.VISIBLE);
                            YoYo.with(Techniques.SlideInUp)
                                    .duration(300)
                                    .playOn(proceedcard);

                            code1.setKeyListener(null);
                            code2.setKeyListener(null);
                            code3.setKeyListener(null);
                            code4.setKeyListener(null);
                            code5.setKeyListener(null);
                            code6.setKeyListener(null);
                            code1.clearFocus();
                            code2.clearFocus();
                            code3.clearFocus();
                            code4.clearFocus();
                            code5.clearFocus();
                            code6.clearFocus();

                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(constraintLayout.getWindowToken(), 0);

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Sneaker.with(VerifyOtp.this)
                                    .setTitle("Invalid OTP Entered", R.color.white)
                                    .setMessage("Please Check Your OTP", R.color.white)
                                    .setDuration(2000)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setIcon(R.drawable.info,R.color.white)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200);

                            codesentto.setText("WRONG OTP");
                            codesentto.setTextColor(Color.RED);
                            code1.setText("");
                            code2.setText("");
                            code3.setText("");
                            code4.setText("");
                            code5.setText("");
                            code6.setText("");
                            code6.clearFocus();
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(constraintLayout.getWindowToken(), 0);


                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        customType(VerifyOtp.this, "left-to-right");


    }
}