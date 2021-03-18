package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static maes.tech.intentanim.CustomIntent.customType;

public class MainActivity extends AppCompatActivity {

    TextView login,newlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.login);
        newlogin = findViewById(R.id.newlogin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Login.class));
                customType(MainActivity.this,"fadein-to-fadeout");
            }
        });


        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        if(snapshot.child("name").exists())
                        {
                            startActivity(new Intent(MainActivity.this,Home.class));
                            finish();
                            customType(MainActivity.this,"fadein-to-fadeout");
                        }
                        else if(snapshot.child("phone").exists())
                        {
                            Intent intent=(new Intent(MainActivity.this,ProfileSetup.class));
                            intent.putExtra("phone",snapshot.child("phone").getValue(String.class));
                            startActivity(intent);
                            finish();
                            customType(MainActivity.this,"left-to-right");
                        }
                    }
                    else {
                        Intent intent=(new Intent(MainActivity.this,ProfileSetup.class));
                        intent.putExtra("phone",snapshot.child("phone").getValue(String.class));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        customType(MainActivity.this,"left-to-right");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        newlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,NewLogin.class));
                customType(MainActivity.this,"fadein-to-fadeout");
            }
        });

    }


}