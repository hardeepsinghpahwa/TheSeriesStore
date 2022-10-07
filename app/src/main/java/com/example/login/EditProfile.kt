package com.example.login


import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
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
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.valdesekamdem.library.mdtoast.MDToast
import android.app.DatePickerDialog
import com.google.firebase.storage.UploadTask
import android.app.DatePickerDialog.OnDateSetListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import kotlin.Throws
import android.provider.OpenableColumns
import android.text.TextUtils
import android.app.AlertDialog
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.util.Patterns
import android.view.*
import android.widget.*
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class EditProfile constructor() : AppCompatActivity() {
   lateinit var name: TextInputEditText
    lateinit var email: TextInputEditText
    lateinit var phone: TextInputEditText
    lateinit var save: TextView
    lateinit var changepassword: TextView
    lateinit var radioGroup: RadioGroup
    var gender: String = ""
    lateinit var profilepic: ImageView
    lateinit var image:Uri
    lateinit var male: RadioButton
    lateinit var female: RadioButton
    lateinit var others: RadioButton
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    lateinit var birthday: TextView
    lateinit var datePickerDialog: DatePickerDialog
    lateinit var uploadTask: UploadTask
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        val c: Calendar = Calendar.getInstance()
        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)
        datePickerDialog = DatePickerDialog(this@EditProfile,
                object : OnDateSetListener {
                    public override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
                        val handler: Handler = Handler(Looper.getMainLooper())
                        handler.postDelayed(object : Runnable {
                            public override fun run() {
                                val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd MMMM yyyy")
                                val calendar: Calendar = GregorianCalendar(year, month, dayOfMonth)
                                birthday!!.setText(simpleDateFormat.format(calendar.getTime()))
                                datePickerDialog!!.dismiss()
                            }
                        }, 100)
                    }
                }, mYear, mMonth, mDay)
        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        phone = findViewById(R.id.phone)
        birthday = findViewById(R.id.birthday)
        save = findViewById(R.id.save)
        changepassword = findViewById(R.id.changepassword)
        radioGroup = findViewById(R.id.genderradiogrp)
        profilepic = findViewById(R.id.profilepic)
        male = findViewById(R.id.male)
        female = findViewById(R.id.female)
        others = findViewById(R.id.others)
        phone.setOnTouchListener(object : OnTouchListener {
            public override fun onTouch(v: View, event: MotionEvent): Boolean {
                MDToast.makeText(getApplicationContext(), "Phone number cant be changed", MDToast.TYPE_INFO, MDToast.LENGTH_SHORT).show()
                return false
            }
        })
        birthday.setOnTouchListener(object : OnTouchListener {
            public override fun onTouch(v: View, event: MotionEvent): Boolean {
                datePickerDialog!!.getDatePicker().setMaxDate(Date().getTime())
                datePickerDialog!!.show()
                return true
            }
        })
        findViewById<View>(R.id.tt).setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                val c: Calendar = Calendar.getInstance()
                mYear = c.get(Calendar.YEAR)
                mMonth = c.get(Calendar.MONTH)
                mDay = c.get(Calendar.DAY_OF_MONTH)
                datePickerDialog = DatePickerDialog(this@EditProfile,
                        object : OnDateSetListener {
                            public override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
                                val handler: Handler = Handler(Looper.getMainLooper())
                                handler.postDelayed(object : Runnable {
                                    public override fun run() {
                                        val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd MMMM yyyy")
                                        val calendar: Calendar = GregorianCalendar(year, month, dayOfMonth)
                                        birthday.setText(simpleDateFormat.format(calendar.getTime()))
                                        datePickerDialog!!.dismiss()
                                    }
                                }, 100)
                            }
                        }, mYear, mMonth, mDay)
                datePickerDialog!!.show()
            }
        })
        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(object : ValueEventListener {
            public override fun onDataChange(snapshot: DataSnapshot) {
                name.setText(snapshot.child("name").getValue(String::class.java))
                email.setText(snapshot.child("email").getValue(String::class.java))
                phone.setText(snapshot.child("phone").getValue(String::class.java))
                if (snapshot.child("gender").exists()) {
                    if ((snapshot.child("gender").getValue(String::class.java) == "Male")) {
                        male.setChecked(true)
                        male.setTextColor(Color.WHITE)
                    } else if ((snapshot.child("gender").getValue(String::class.java) == "Female")) {
                        female.setChecked(true)
                        female.setTextColor(Color.WHITE)
                    } else if ((snapshot.child("gender").getValue(String::class.java) == "Others")) {
                        others.setChecked(true)
                        others.setTextColor(Color.WHITE)
                    }
                }
                if (snapshot.child("birthday").exists()) {
                    birthday.setText(snapshot.child("birthday").getValue(String::class.java))
                    val sdf: SimpleDateFormat = SimpleDateFormat("dd MMMM yyyy")
                    var date: Date? = null
                    try {
                        date = sdf.parse(snapshot.child("birthday").getValue(String::class.java))
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                    val cal: Calendar = Calendar.getInstance()
                    cal.setTime(date)
                    mYear = cal.get(Calendar.YEAR)
                    mMonth = cal.get(Calendar.MONTH)
                    mDay = cal.get(Calendar.DAY_OF_MONTH)
                    datePickerDialog = DatePickerDialog(this@EditProfile,
                            object : OnDateSetListener {
                                public override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
                                    val handler: Handler = Handler(Looper.getMainLooper())
                                    handler.postDelayed(object : Runnable {
                                        public override fun run() {
                                            val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd MMMM yyyy")
                                            val calendar: Calendar = GregorianCalendar(year, month, dayOfMonth)
                                            birthday.setText(simpleDateFormat.format(calendar.getTime()))
                                            datePickerDialog!!.dismiss()
                                        }
                                    }, 100)
                                }
                            }, mYear, mMonth, mDay)
                }
                if (snapshot.child("profilepic").exists()) {
                    Glide.with(getApplicationContext()).load(snapshot.child("profilepic").getValue(String::class.java)).into(profilepic)
                }
            }

            public override fun onCancelled(error: DatabaseError) {}
        })
        profilepic.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                ImagePicker.with(this@EditProfile)
                        .cropSquare() //Crop image(Optional), Check Customization for more option
                        .compress(200) //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(720, 720) //Final image resolution will be less than 1080 x 1080(Optional)
                        .start()
            }
        })
        phone.setKeyListener(null)
        birthday.setKeyListener(null)
        radioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            public override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
                val radioButton: RadioButton = group.findViewById(checkedId)
                gender = radioButton.getText().toString()
                if ((gender == "Male")) {
                    radioButton.setTextColor(Color.WHITE)
                    female.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey))
                    others.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey))
                } else if ((gender == "Female")) {
                    radioButton.setTextColor(Color.WHITE)
                    male.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey))
                    others.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey))
                } else if ((gender == "Others")) {
                    radioButton.setTextColor(Color.WHITE)
                    female.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey))
                    male.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey))
                }
            }
        })
        changepassword.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                startActivity(Intent(this@EditProfile, ChangePassword::class.java))
                CustomIntent.customType(this@EditProfile, "left-to-right")
            }
        })
        findViewById<View>(R.id.back).setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                onBackPressed()
            }
        })
        save.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                if ((name.getText().toString() == "")) {
                    Sneaker.with(this@EditProfile)
                            .setTitle("Enter you name", R.color.white)
                            .setMessage("Name cant be empty", R.color.white)
                            .setIcon(R.drawable.info, R.color.white)
                            .setDuration(2000)
                            .autoHide(true)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200)
                } else if ((email.getText().toString() == "")) {
                    Sneaker.with(this@EditProfile)
                            .setTitle("Enter you email", R.color.white)
                            .setMessage("Email cant be empty", R.color.white)
                            .setDuration(2000)
                            .setIcon(R.drawable.info, R.color.white)
                            .autoHide(true)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200)
                } else if (!isValidEmail(email.getText().toString())) {
                    Sneaker.with(this@EditProfile)
                            .setTitle("Invalid Email", R.color.white)
                            .setMessage("Enter a valid email", R.color.white)
                            .setDuration(2000)
                            .setIcon(R.drawable.info, R.color.white)
                            .autoHide(true)
                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setCornerRadius(10, 0)
                            .sneak(R.color.teal_200)
                } else {
                    if (image == null) {
                        val map= mutableMapOf<String?, Any?>()
                        map.put("name", name.getText().toString())
                        map.put("email", email.getText().toString())
                        if (!(gender == "")) {
                            map.put("gender", gender)
                        }
                        if (!(birthday.getText().toString() == "")) {
                            map.put("birthday", birthday.getText().toString())
                        }
                        val alertDialog: AlertDialog = SpotsDialog.Builder()
                                .setCancelable(false)
                                .setContext(this@EditProfile)
                                .setTheme(R.style.ProgressDialog)
                                .setMessage("Saving Your Details")
                                .build()
                        alertDialog.show()

                        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(object : OnCompleteListener<Void?> {
                            public override fun onComplete(p0: Task<Void?>) {
                                if (p0.isSuccessful()) {
                                    alertDialog.dismiss()
                                    Sneaker.with(this@EditProfile)
                                            .setTitle("Changes Saved", R.color.white)
                                            .setMessage("You details have been updated", R.color.white)
                                            .setDuration(2000)
                                            .setIcon(R.drawable.info, R.color.white)
                                            .autoHide(true)
                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                            .setCornerRadius(10, 0)
                                            .sneak(R.color.green)
                                } else {
                                    alertDialog.dismiss()
                                    Sneaker.with(this@EditProfile)
                                            .setTitle("Error Saving Details", R.color.white)
                                            .setMessage("Try Again Please!", R.color.white)
                                            .setDuration(2000)
                                            .autoHide(true)
                                            .setIcon(R.drawable.info, R.color.white)
                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                            .setCornerRadius(10, 0)
                                            .sneak(R.color.teal_200)
                                }
                            }
                        })
                    } else {
                        val alertDialog: AlertDialog = SpotsDialog.Builder()
                                .setCancelable(false)
                                .setContext(this@EditProfile)
                                .setTheme(R.style.ProgressDialog)
                                .setMessage("Saving Your Details")
                                .build()
                        alertDialog.show()
                        val ref: StorageReference = FirebaseStorage.getInstance().getReference().child("Profile Pictures").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child((getFileName(image!!))!!)
                        uploadTask = ref.putFile(image!!)
                        val urlTask: Task<Uri> = uploadTask!!.continueWithTask(object : Continuation<UploadTask.TaskSnapshot?, Task<Uri>> {
                            @Throws(Exception::class)
                            public override fun then(task: Task<UploadTask.TaskSnapshot?>): Task<Uri> {
                                if (!task.isSuccessful()) {
                                    throw (task.getException())!!
                                }

                                // Continue with the task to get the download URL
                                return ref.getDownloadUrl()
                            }
                        }).addOnCompleteListener(object : OnCompleteListener<Uri?> {
                            public override fun onComplete(task: Task<Uri?>) {
                                if (task.isSuccessful()) {
                                    val downloadUri: Uri? = task.getResult()
                                    val map= mutableMapOf<String?, Any?>()
                                    map.put("name", name.getText().toString())
                                    map.put("email", email.getText().toString())
                                    if (!(gender == "")) {
                                        map.put("gender", gender)
                                    }
                                    if (!(birthday.getText().toString() == "")) {
                                        map.put("birthday", birthday.getText().toString())
                                    }
                                    map.put("profilepic", downloadUri.toString())
                                    FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(object : OnCompleteListener<Void?> {
                                        public override fun onComplete(p0: Task<Void?>) {
                                            if (p0.isSuccessful()) {
                                                alertDialog.dismiss()
                                                Sneaker.with(this@EditProfile)
                                                        .setTitle("Changes Saved", R.color.white)
                                                        .setMessage("You details have been updated", R.color.white)
                                                        .setDuration(2000)
                                                        .setIcon(R.drawable.info, R.color.white)
                                                        .autoHide(true)
                                                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                        .setCornerRadius(10, 0)
                                                        .sneak(R.color.green)
                                            } else {
                                                alertDialog.dismiss()
                                                Sneaker.with(this@EditProfile)
                                                        .setTitle("Error Saving Details", R.color.white)
                                                        .setMessage("Try Again Please!", R.color.white)
                                                        .setDuration(2000)
                                                        .autoHide(true)
                                                        .setIcon(R.drawable.info, R.color.white)
                                                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                        .setCornerRadius(10, 0)
                                                        .sneak(R.color.teal_200)
                                            }
                                        }
                                    })
                                } else {
                                    alertDialog.dismiss()
                                    Sneaker.with(this@EditProfile)
                                            .setTitle("Error Uploading Profile Picture", R.color.white)
                                            .setMessage("Try Again Please!", R.color.white)
                                            .setDuration(2000)
                                            .autoHide(true)
                                            .setIcon(R.drawable.info, R.color.white)
                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                            .setCornerRadius(10, 0)
                                            .sneak(R.color.teal_200)
                                }
                            }
                        })
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            profilepic!!.setImageURI(data.getData())
            image = data.getData()!!
        } else {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("Range")
    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if ((uri.getScheme() == "content")) {
            val cursor: Cursor? = getContentResolver().query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.getPath()
            val cut: Int = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    public override fun onBackPressed() {
        super.onBackPressed()
        CustomIntent.customType(this@EditProfile, "right-to-left")
    }

    companion object {
        fun isValidEmail(target: CharSequence?): Boolean {
            return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches())
        }
    }
}