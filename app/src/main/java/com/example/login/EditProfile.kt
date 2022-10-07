package com.example.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.irozon.sneaker.Sneaker;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import dmax.dialog.SpotsDialog;

import static maes.tech.intentanim.CustomIntent.customType;

public class EditProfile extends AppCompatActivity {


    TextInputEditText name, email, phone;
    TextView save, changepassword;
    RadioGroup radioGroup;
    String gender = "";
    ImageView profilepic;
    Uri image;
    RadioButton male, female, others;
    private int mYear, mMonth, mDay;
    TextView birthday;
    DatePickerDialog datePickerDialog = null;
    UploadTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(EditProfile.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
                                Calendar calendar = new GregorianCalendar(year, month, dayOfMonth);
                                birthday.setText(simpleDateFormat.format(calendar.getTime()));
                                datePickerDialog.dismiss();
                            }
                        }, 100);

                    }
                }, mYear, mMonth, mDay);


        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        birthday = findViewById(R.id.birthday);
        save = findViewById(R.id.save);
        changepassword = findViewById(R.id.changepassword);
        radioGroup = findViewById(R.id.genderradiogrp);
        profilepic = findViewById(R.id.profilepic);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        others = findViewById(R.id.others);


        phone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MDToast.makeText(getApplicationContext(), "Phone number cant be changed", MDToast.TYPE_INFO, MDToast.LENGTH_SHORT).show();
                return false;
            }
        });

        birthday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.show();

                return true;
            }
        });

        findViewById(R.id.tt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                datePickerDialog = new DatePickerDialog(EditProfile.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                                final Handler handler = new Handler(Looper.getMainLooper());
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
                                        Calendar calendar = new GregorianCalendar(year, month, dayOfMonth);
                                        birthday.setText(simpleDateFormat.format(calendar.getTime()));
                                        datePickerDialog.dismiss();
                                    }
                                }, 100);

                            }
                        }, mYear, mMonth, mDay);


                datePickerDialog.show();

            }
        });


        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText(snapshot.child("name").getValue(String.class));
                email.setText(snapshot.child("email").getValue(String.class));
                phone.setText(snapshot.child("phone").getValue(String.class));
                if (snapshot.child("gender").exists()) {
                    if (snapshot.child("gender").getValue(String.class).equals("Male")) {
                        male.setChecked(true);
                        male.setTextColor(Color.WHITE);
                    } else if (snapshot.child("gender").getValue(String.class).equals("Female")) {
                        female.setChecked(true);
                        female.setTextColor(Color.WHITE);
                    } else if (snapshot.child("gender").getValue(String.class).equals("Others")) {
                        others.setChecked(true);
                        others.setTextColor(Color.WHITE);
                    }
                }

                if (snapshot.child("birthday").exists()) {
                    birthday.setText(snapshot.child("birthday").getValue(String.class));
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
                    Date date = null;
                    try {
                        date = sdf.parse(snapshot.child("birthday").getValue(String.class));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);

                    mYear = cal.get(Calendar.YEAR);
                    mMonth = cal.get(Calendar.MONTH);
                    mDay = cal.get(Calendar.DAY_OF_MONTH);

                    datePickerDialog = new DatePickerDialog(EditProfile.this,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                                    final Handler handler = new Handler(Looper.getMainLooper());
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
                                            Calendar calendar = new GregorianCalendar(year, month, dayOfMonth);
                                            birthday.setText(simpleDateFormat.format(calendar.getTime()));
                                            datePickerDialog.dismiss();
                                        }
                                    }, 100);

                                }
                            }, mYear, mMonth, mDay);


                }
                if (snapshot.child("profilepic").exists()) {
                    Glide.with(getApplicationContext()).load(snapshot.child("profilepic").getValue(String.class)).into(profilepic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(EditProfile.this)
                        .cropSquare()                //Crop image(Optional), Check Customization for more option
                        .compress(200)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(720, 720)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        phone.setKeyListener(null);
        birthday.setKeyListener(null);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                gender = radioButton.getText().toString();

                if (gender.equals("Male")) {
                    radioButton.setTextColor(Color.WHITE);
                    female.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey));
                    others.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey));

                } else if (gender.equals("Female")) {
                    radioButton.setTextColor(Color.WHITE);
                    male.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey));
                    others.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey));

                } else if (gender.equals("Others")) {
                    radioButton.setTextColor(Color.WHITE);
                    female.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey));
                    male.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey));

                }

            }
        });


        changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(EditProfile.this,ChangePassword.class));
                customType(EditProfile.this,"left-to-right");

            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (name.getText().toString().equals("")) {
                    Sneaker.with(EditProfile.this)
                            .setTitle("Enter you name", R.color.white)
                            .setMessage("Name cant be empty", R.color.white)
                            .setIcon(R.drawable.info, R.color.white)
                            .setDuration(2000)
                            .autoHide(true)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200);
                } else if (email.getText().toString().equals("")) {
                    Sneaker.with(EditProfile.this)
                            .setTitle("Enter you email", R.color.white)
                            .setMessage("Email cant be empty", R.color.white)
                            .setDuration(2000)
                            .setIcon(R.drawable.info, R.color.white)
                            .autoHide(true)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200);
                } else if (!isValidEmail(email.getText().toString())) {
                    Sneaker.with(EditProfile.this)
                            .setTitle("Invalid Email", R.color.white)
                            .setMessage("Enter a valid email", R.color.white)
                            .setDuration(2000)
                            .setIcon(R.drawable.info, R.color.white)
                            .autoHide(true)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200);
                } else {

                    if (image == null) {

                        Map map = new HashMap();
                        map.put("name", name.getText().toString());
                        map.put("email", email.getText().toString());
                        if (!gender.equals("")) {
                            map.put("gender", gender);
                        }
                        if (!birthday.getText().toString().equals("")) {
                            map.put("birthday", birthday.getText().toString());
                        }

                        final AlertDialog alertDialog = new SpotsDialog.Builder()
                                .setCancelable(false)
                                .setContext(EditProfile.this)
                                .setTheme(R.style.ProgressDialog)
                                .setMessage("Saving Your Details")
                                .build();

                        alertDialog.show();


                        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    alertDialog.dismiss();
                                    Sneaker.with(EditProfile.this)
                                            .setTitle("Changes Saved", R.color.white)
                                            .setMessage("You details have been updated", R.color.white)
                                            .setDuration(2000)
                                            .setIcon(R.drawable.info, R.color.white)
                                            .autoHide(true)
                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                            .setCornerRadius(10, 0)
                                            .sneak(R.color.green);
                                } else {
                                    alertDialog.dismiss();
                                    Sneaker.with(EditProfile.this)
                                            .setTitle("Error Saving Details", R.color.white)
                                            .setMessage("Try Again Please!", R.color.white)
                                            .setDuration(2000)
                                            .autoHide(true)
                                            .setIcon(R.drawable.info, R.color.white)
                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                            .setCornerRadius(10, 0)
                                            .sneak(R.color.teal_200);
                                }
                            }
                        });

                    } else {
                        final AlertDialog alertDialog = new SpotsDialog.Builder()
                                .setCancelable(false)
                                .setContext(EditProfile.this)
                                .setTheme(R.style.ProgressDialog)
                                .setMessage("Saving Your Details")
                                .build();

                        alertDialog.show();

                        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("Profile Pictures").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(getFileName(image));
                        uploadTask = ref.putFile(image);

                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return ref.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();

                                    Map map = new HashMap();
                                    map.put("name", name.getText().toString());
                                    map.put("email", email.getText().toString());
                                    if (!gender.equals("")) {
                                        map.put("gender", gender);
                                    }
                                    if (!birthday.getText().toString().equals("")) {
                                        map.put("birthday", birthday.getText().toString());
                                    }
                                    map.put("profilepic", downloadUri.toString());


                                    FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful()) {
                                                alertDialog.dismiss();
                                                Sneaker.with(EditProfile.this)
                                                        .setTitle("Changes Saved", R.color.white)
                                                        .setMessage("You details have been updated", R.color.white)
                                                        .setDuration(2000)
                                                        .setIcon(R.drawable.info, R.color.white)
                                                        .autoHide(true)
                                                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                        .setCornerRadius(10, 0)
                                                        .sneak(R.color.green);
                                            } else {
                                                alertDialog.dismiss();
                                                Sneaker.with(EditProfile.this)
                                                        .setTitle("Error Saving Details", R.color.white)
                                                        .setMessage("Try Again Please!", R.color.white)
                                                        .setDuration(2000)
                                                        .autoHide(true)
                                                        .setIcon(R.drawable.info, R.color.white)
                                                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                        .setCornerRadius(10, 0)
                                                        .sneak(R.color.teal_200);
                                            }
                                        }
                                    });

                                } else {
                                    alertDialog.dismiss();
                                    Sneaker.with(EditProfile.this)
                                            .setTitle("Error Uploading Profile Picture", R.color.white)
                                            .setMessage("Try Again Please!", R.color.white)
                                            .setDuration(2000)
                                            .autoHide(true)
                                            .setIcon(R.drawable.info, R.color.white)
                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                            .setCornerRadius(10, 0)
                                            .sneak(R.color.teal_200);

                                }
                            }
                        });


                    }
                }
            }
        });
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            profilepic.setImageURI(data.getData());
            image = data.getData();
        } else {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(EditProfile.this,"right-to-left");
    }
}