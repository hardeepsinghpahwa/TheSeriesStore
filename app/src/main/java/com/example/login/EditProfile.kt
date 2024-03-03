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
import androidx.databinding.DataBindingUtil
import com.example.login.databinding.ActivityEditProfileBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class EditProfile : AppCompatActivity() {

    var gender: String = ""
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    lateinit var datePickerDialog: DatePickerDialog
    lateinit var binding: ActivityEditProfileBinding
    lateinit var uploadTask: UploadTask
    var image: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        binding.executePendingBindings()
        val c: Calendar = Calendar.getInstance()
        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)
        datePickerDialog = DatePickerDialog(this@EditProfile,
            object : OnDateSetListener {
                override fun onDateSet(
                    view: DatePicker,
                    year: Int,
                    month: Int,
                    dayOfMonth: Int
                ) {
                    val handler: Handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        val simpleDateFormat: SimpleDateFormat =
                            SimpleDateFormat("dd MMMM yyyy")
                        val calendar: Calendar = GregorianCalendar(year, month, dayOfMonth)
                        binding.birthday.setText(simpleDateFormat.format(calendar.time))
                        datePickerDialog.dismiss()
                    }, 100)
                }
            }, mYear, mMonth, mDay
        )

        binding.phone.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                MDToast.makeText(
                    applicationContext,
                    "Phone number cant be changed",
                    MDToast.TYPE_INFO,
                    MDToast.LENGTH_SHORT
                ).show()
                return false
            }
        })
        binding.birthday.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                datePickerDialog.datePicker.maxDate = Date().time
                datePickerDialog.show()
                return true
            }
        })
        findViewById<View>(R.id.tt).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val c: Calendar = Calendar.getInstance()
                mYear = c.get(Calendar.YEAR)
                mMonth = c.get(Calendar.MONTH)
                mDay = c.get(Calendar.DAY_OF_MONTH)
                datePickerDialog = DatePickerDialog(this@EditProfile,
                    object : OnDateSetListener {
                        override fun onDateSet(
                            view: DatePicker,
                            year: Int,
                            month: Int,
                            dayOfMonth: Int
                        ) {
                            val handler: Handler = Handler(Looper.getMainLooper())
                            handler.postDelayed(object : Runnable {
                                override fun run() {
                                    val simpleDateFormat: SimpleDateFormat =
                                        SimpleDateFormat("dd MMMM yyyy")
                                    val calendar: Calendar =
                                        GregorianCalendar(year, month, dayOfMonth)
                                    binding.birthday.setText(simpleDateFormat.format(calendar.time))
                                    datePickerDialog.dismiss()
                                }
                            }, 100)
                        }
                    }, mYear, mMonth, mDay
                )
                datePickerDialog.show()
            }
        })
        FirebaseAuth.getInstance().currentUser?.let {
            FirebaseDatabase.getInstance().reference.child("Profiles")
                .child(it.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        binding.name.setText(snapshot.child("name").getValue(String::class.java))
                        binding.email.setText(snapshot.child("email").getValue(String::class.java))
                        binding.phone.setText(snapshot.child("phone").getValue(String::class.java))
                        if (snapshot.child("gender").exists()) {
                            if ((snapshot.child("gender").getValue(String::class.java) == "Male")) {
                                binding.male.isChecked = true
                                binding.male.setTextColor(Color.WHITE)
                            } else if ((snapshot.child("gender")
                                    .getValue(String::class.java) == "Female")
                            ) {
                                binding.female.isChecked = true
                                binding.female.setTextColor(Color.WHITE)
                            } else if ((snapshot.child("gender")
                                    .getValue(String::class.java) == "Others")
                            ) {
                                binding.others.isChecked = true
                                binding.others.setTextColor(Color.WHITE)
                            }
                        }
                        if (snapshot.child("birthday").exists()) {
                            binding.birthday.setText(
                                snapshot.child("birthday").getValue(String::class.java)
                            )
                            val sdf: SimpleDateFormat = SimpleDateFormat("dd MMMM yyyy")
                            var date: Date? = null
                            try {
                                date =
                                    sdf.parse(snapshot.child("birthday").getValue(String::class.java))
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            val cal: Calendar = Calendar.getInstance()
                            cal.time = date
                            mYear = cal.get(Calendar.YEAR)
                            mMonth = cal.get(Calendar.MONTH)
                            mDay = cal.get(Calendar.DAY_OF_MONTH)
                            datePickerDialog = DatePickerDialog(this@EditProfile,
                                object : OnDateSetListener {
                                    override fun onDateSet(
                                        view: DatePicker,
                                        year: Int,
                                        month: Int,
                                        dayOfMonth: Int
                                    ) {
                                        val handler: Handler = Handler(Looper.getMainLooper())
                                        handler.postDelayed(object : Runnable {
                                            override fun run() {
                                                val simpleDateFormat: SimpleDateFormat =
                                                    SimpleDateFormat("dd MMMM yyyy")
                                                val calendar: Calendar =
                                                    GregorianCalendar(year, month, dayOfMonth)
                                                binding.birthday.setText(
                                                    simpleDateFormat.format(
                                                        calendar.time
                                                    )
                                                )
                                                datePickerDialog.dismiss()
                                            }
                                        }, 100)
                                    }
                                }, mYear, mMonth, mDay
                            )
                        }
                        if (snapshot.child("profilepic").exists()) {
                            Glide.with(applicationContext)
                                .load(snapshot.child("profilepic").getValue(String::class.java))
                                .into(binding.profilepic)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
        binding.profilepic.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                ImagePicker.with(this@EditProfile)
                    .cropSquare() //Crop image(Optional), Check Customization for more option
                    .compress(200) //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(
                        720,
                        720
                    ) //Final image resolution will be less than 1080 x 1080(Optional)
                    .start()
            }
        })
        binding.phone.keyListener = null
        binding.birthday.keyListener = null
        binding.genderradiogrp.setOnCheckedChangeListener(object :
            RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
                val radioButton: RadioButton = group.findViewById(checkedId)
                gender = radioButton.text.toString()
                if ((gender == "Male")) {
                    radioButton.setTextColor(Color.WHITE)
                    binding.female.setTextColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.darkgrey
                        )
                    )
                    binding.others.setTextColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.darkgrey
                        )
                    )
                } else if ((gender == "Female")) {
                    radioButton.setTextColor(Color.WHITE)
                    binding.male.setTextColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.darkgrey
                        )
                    )
                    binding.others.setTextColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.darkgrey
                        )
                    )
                } else if ((gender == "Others")) {
                    radioButton.setTextColor(Color.WHITE)
                    binding.female.setTextColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.darkgrey
                        )
                    )
                    binding.male.setTextColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.darkgrey
                        )
                    )
                }
            }
        })
        binding.changepassword.setOnClickListener {
            startActivity(Intent(this@EditProfile, ChangePassword::class.java))
            CustomIntent.customType(this@EditProfile, "left-to-right")
        }
        findViewById<View>(R.id.back).setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.save.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                if ((binding.name.text.toString() == "")) {
                    Sneaker.with(this@EditProfile)
                        .setTitle("Enter you name", R.color.white)
                        .setMessage("Name cant be empty", R.color.white)
                        .setIcon(R.drawable.info, R.color.white)
                        .setDuration(2000)
                        .autoHide(true)
                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setCornerRadius(10, 0)
                        .sneak(R.color.teal_200)
                } else if ((binding.email.text.toString() == "")) {
                    Sneaker.with(this@EditProfile)
                        .setTitle("Enter you email", R.color.white)
                        .setMessage("Email cant be empty", R.color.white)
                        .setDuration(2000)
                        .setIcon(R.drawable.info, R.color.white)
                        .autoHide(true)
                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setCornerRadius(10, 0)
                        .sneak(R.color.teal_200)
                } else if (!isValidEmail(binding.email.text.toString())) {
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
                        val map = mutableMapOf<String?, Any?>()
                        map.put("name", binding.name.text.toString())
                        map.put("email", binding.email.text.toString())
                        if (!(gender == "")) {
                            map.put("gender", gender)
                        }
                        if (!(binding.birthday.text.toString() == "")) {
                            map.put("birthday", binding.birthday.text.toString())
                        }
                        val alertDialog: AlertDialog = SpotsDialog.Builder()
                            .setCancelable(false)
                            .setContext(this@EditProfile)
                            .setTheme(R.style.ProgressDialog)
                            .setMessage("Saving Your Details")
                            .build()
                        alertDialog.show()

                        FirebaseDatabase.getInstance().reference.child("Profiles")
                            .child(FirebaseAuth.getInstance().currentUser.uid)
                            .updateChildren(map)
                            .addOnCompleteListener(object : OnCompleteListener<Void?> {
                                override fun onComplete(p0: Task<Void?>) {
                                    if (p0.isSuccessful) {
                                        alertDialog.dismiss()
                                        Sneaker.with(this@EditProfile)
                                            .setTitle("Changes Saved", R.color.white)
                                            .setMessage(
                                                "You details have been updated",
                                                R.color.white
                                            )
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
                        val ref: StorageReference =
                            FirebaseStorage.getInstance().reference.child("Profile Pictures")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child((getFileName(image!!)))
                        uploadTask = ref.putFile(image!!)
                        val urlTask: Task<Uri> = uploadTask.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                throw (task.exception)!!
                            }

                            // Continue with the task to get the download URL
                            ref.downloadUrl
                        }.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val downloadUri: Uri? = task.result
                                val map = mutableMapOf<String?, Any?>()
                                map["name"] = binding.name.text.toString()
                                map["email"] = binding.email.text.toString()
                                if (gender != "") {
                                    map["gender"] = gender
                                }
                                if (binding.birthday.text.toString() != "") {
                                    map["birthday"] = binding.birthday.text.toString()
                                }
                                map["profilepic"] = downloadUri.toString()
                                FirebaseDatabase.getInstance().reference.child("Profiles")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .updateChildren(map)
                                    .addOnCompleteListener { p0 ->
                                        if (p0.isSuccessful) {
                                            alertDialog.dismiss()
                                            Sneaker.with(this@EditProfile)
                                                .setTitle("Changes Saved", R.color.white)
                                                .setMessage(
                                                    "You details have been updated",
                                                    R.color.white
                                                )
                                                .setDuration(2000)
                                                .setIcon(R.drawable.info, R.color.white)
                                                .autoHide(true)
                                                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                .setCornerRadius(10, 0)
                                                .sneak(R.color.green)
                                        } else {
                                            alertDialog.dismiss()
                                            Sneaker.with(this@EditProfile)
                                                .setTitle(
                                                    "Error Saving Details",
                                                    R.color.white
                                                )
                                                .setMessage(
                                                    "Try Again Please!",
                                                    R.color.white
                                                )
                                                .setDuration(2000)
                                                .autoHide(true)
                                                .setIcon(R.drawable.info, R.color.white)
                                                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                .setCornerRadius(10, 0)
                                                .sneak(R.color.teal_200)
                                        }
                                    }
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
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            binding.profilepic.setImageURI(data.data)
            image = data.data!!
        } else {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("Range")
    fun getFileName(uri: Uri): String {
        var result: String? = null
        if ((uri.scheme == "content")) {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut: Int = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    override fun onBackPressed() {
        super.onBackPressed()
        CustomIntent.customType(this@EditProfile, "right-to-left")
    }

    companion object {
        fun isValidEmail(target: CharSequence?): Boolean {
            return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target!!).matches())
        }
    }
}