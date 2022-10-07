package com.example.login


import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.constraintlayout.widget.ConstraintLayout
import com.firebase.ui.database.FirebaseRecyclerAdapter
import android.os.Bundle
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import android.view.View.OnTouchListener
import com.irozon.sneaker.Sneaker
import dmax.dialog.SpotsDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.OnCompleteListener
import com.baoyachi.stepview.HorizontalStepView
import com.baoyachi.stepview.bean.StepBean
import android.content.Intent
import maes.tech.intentanim.CustomIntent
import com.firebase.ui.database.FirebaseRecyclerOptions
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import dev.shreyaspatil.MaterialDialog.AbstractDialog
import androidx.recyclerview.widget.LinearLayoutManager
import net.igenius.customcheckbox.CustomCheckBox

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color

import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog

import com.firebase.ui.database.SnapshotParser
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*

import java.util.*

class Address() : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var noaddresstext: TextView
    lateinit var addnew: TextView
    lateinit var continuetopay: TextView
    lateinit var noaddress: ImageView
    lateinit var progresslayout: ConstraintLayout
    var posi = 0
    var type = ""
    var name: String? = null
    lateinit var phone: String
    lateinit var address: String
    lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<addressdetail, AddressViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)
        findViewById<View>(R.id.back).setOnClickListener(View.OnClickListener { onBackPressed() })
        recyclerView = findViewById(R.id.addressrecyview)
        noaddresstext = findViewById(R.id.noaddressestext)
        addnew = findViewById(R.id.addnew)
        continuetopay = findViewById(R.id.continuetopay)
        noaddress = findViewById(R.id.noaddresses)
        progresslayout = findViewById(R.id.progresslayout)

        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        addnew.setOnClickListener {

        }

        addnew.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                addnew.setEnabled(false)
                val name: EditText
                val address: EditText
                val city: EditText
                val phone: EditText
                val pincode: EditText
                val state: TextView
                val save: TextView
                val home: TextView
                val office: TextView
                val cross: ImageView
                val dialog = Dialog(this@Address)
                dialog.setContentView(R.layout.addaddress)
                dialog.setCanceledOnTouchOutside(false)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                dialog.window!!.attributes.windowAnimations = R.style.AppTheme_UpDown
                name = dialog.findViewById(R.id.name)
                address = dialog.findViewById(R.id.address)
                city = dialog.findViewById(R.id.city)
                state = dialog.findViewById(R.id.state)
                save = dialog.findViewById(R.id.save)
                phone = dialog.findViewById(R.id.phone)
                pincode = dialog.findViewById(R.id.pincode)
                cross = dialog.findViewById(R.id.cross)
                home = dialog.findViewById(R.id.home)
                office = dialog.findViewById(R.id.office)
                dialog.setOnDismissListener(object : DialogInterface.OnDismissListener {
                    override fun onDismiss(dialog: DialogInterface) {
                        addnew.setEnabled(true)
                    }
                })
                home.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View) {
                        if ((type == "Home")) {
                            home.setBackgroundResource(R.drawable.stroke2)
                            home.setTextColor(ContextCompat.getColor(applicationContext, R.color.blue))
                        }
                        type = "Home"
                        home.setTextColor(Color.WHITE)
                        home.setBackgroundResource(R.drawable.stroke3)
                        office.setBackgroundResource(R.drawable.stroke2)
                        office.setTextColor(ContextCompat.getColor(applicationContext, R.color.blue))
                    }
                })
                office.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View) {
                        if ((type == "Office")) {
                            office.setBackgroundResource(R.drawable.stroke2)
                            office.setTextColor(ContextCompat.getColor(applicationContext, R.color.blue))
                        }
                        type = "Office"
                        office.setTextColor(Color.WHITE)
                        office.setBackgroundResource(R.drawable.stroke3)
                        home.setBackgroundResource(R.drawable.stroke2)
                        home.setTextColor(ContextCompat.getColor(applicationContext, R.color.blue))
                    }
                })
                cross.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View) {
                        dialog.dismiss()
                    }
                })
                state.keyListener = null
                val listItems = arrayOf("Andaman and Nicobar Islands", "Andra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chandigarh", "Chhattisgarh", "Dadar and Nagar Haveli", "Daman and Diu", "Delhi", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala", "Ladakh", "Lakshadeep", "Madya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Orissa", "Punjab", "Pondicherry", "Rajasthan", "Sikkim", "Tamil Nadu", "Telagana", "Tripura", "Uttarakhand", "Uttar Pradesh", "West Bengal")
                val builder = AlertDialog.Builder(this@Address)
                builder.setTitle("Your State")
                builder.setItems(listItems, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        state.text = listItems.get(which)
                    }
                })
                val dialog1 = builder.create()
                state.setOnTouchListener(object : OnTouchListener {
                    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                        dialog1.show()
                        return false
                    }
                })
                save.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View) {
                        if ((name.text.toString() == "")) {
                            Sneaker.with(this@Address)
                                    .setTitle("Enter Name", R.color.white)
                                    .setMessage("This field cant be empty", R.color.white)
                                    .setDuration(2000)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200)
                        } else if ((phone.text.toString() == "")) {
                            Sneaker.with(this@Address)
                                    .setTitle("Enter Phone", R.color.white)
                                    .setMessage("This field cant be empty", R.color.white)
                                    .setDuration(2000)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200)
                        } else if (phone.text.toString().length < 10) {
                            Sneaker.with(this@Address)
                                    .setTitle("Invalid Phone", R.color.white)
                                    .setMessage("Enter a valid phone number", R.color.white)
                                    .setDuration(2000)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200)
                        } else if ((address.text.toString() == "")) {
                            Sneaker.with(this@Address)
                                    .setTitle("Enter House No,Street,Road,Area,Colony", R.color.white)
                                    .setMessage("This field cant be empty", R.color.white)
                                    .setDuration(2000)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200)
                        } else if ((city.text.toString() == "")) {
                            Sneaker.with(this@Address)
                                    .setTitle("Enter City", R.color.white)
                                    .setMessage("This field cant be empty", R.color.white)
                                    .setDuration(2000)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200)
                        } else if ((state.text.toString() == "")) {
                            Sneaker.with(this@Address)
                                    .setTitle("Select State", R.color.white)
                                    .setMessage("This field cant be empty", R.color.white)
                                    .setDuration(2000)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200)
                        } else if ((pincode.text.toString() == "")) {
                            Sneaker.with(this@Address)
                                    .setTitle("Enter Pincode", R.color.white)
                                    .setMessage("This field cant be empty", R.color.white)
                                    .setDuration(2000)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200)
                        } else if (pincode.text.toString().length < 6) {
                            Sneaker.with(this@Address)
                                    .setTitle("Invalid Pincode", R.color.white)
                                    .setMessage("Enter a valid 6 digit Pincode", R.color.white)
                                    .setDuration(2000)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200)
                        } else {
                            val alertDialog = SpotsDialog.Builder()
                                    .setCancelable(false)
                                    .setContext(this@Address)
                                    .setTheme(R.style.ProgressDialog)
                                    .setMessage("Saving Address")
                                    .build()
                            alertDialog.show()
                            val map = mutableMapOf<String?, Any?>()
                            map["name"] = name.text.toString()
                            map["phone"] = phone.text.toString()
                            map["address"] = address.text.toString()
                            map["city"] = city.text.toString()
                            map["state"] = state.text.toString()
                            map["pincode"] = pincode.text.toString()
                            map["type"] = type
                            FirebaseDatabase.getInstance().reference.child("Profiles").child(FirebaseAuth.getInstance().currentUser!!.uid).child("Addresses").child(UUID.randomUUID().toString()).updateChildren(map).addOnCompleteListener(object : OnCompleteListener<Void?> {
                                @SuppressLint("NotifyDataSetChanged")
                                override fun onComplete(task: Task<Void?>) {
                                    if (task.isSuccessful) {
                                        alertDialog.dismiss()
                                        Sneaker.with(this@Address)
                                                .setTitle("Address Saved", R.color.white)
                                                .setMessage("Your details have been saved", R.color.white)
                                                .setDuration(2000)
                                                .setIcon(R.drawable.info, R.color.white)
                                                .autoHide(true)
                                                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                .setCornerRadius(10, 0)
                                                .sneak(R.color.green)
                                        dialog.dismiss()
                                        firebaseRecyclerAdapter.notifyDataSetChanged()
                                    } else {
                                        alertDialog.dismiss()
                                        Sneaker.with(this@Address)
                                                .setTitle("Error Saving Address", R.color.white)
                                                .setMessage("Please Try Again", R.color.white)
                                                .setDuration(2000)
                                                .setIcon(R.drawable.delete2, R.color.white)
                                                .autoHide(true)
                                                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                .setCornerRadius(10, 0)
                                                .sneak(R.color.teal_200)
                                    }
                                }
                            })
                        }
                    }
                })
                dialog.show()
            }
        })
        val setpview5 = findViewById<View>(R.id.stepsView) as HorizontalStepView
        val stepsBeanList: MutableList<StepBean> = ArrayList()
        val stepBean0 = StepBean("Cart", 1)
        val stepBean1 = StepBean("Address", -1)
        val stepBean2 = StepBean("Payment", 0)
        val stepBean3 = StepBean("Placed", 0)
        if (intent.getStringExtra("pro") != null) {
            findViewById<View>(R.id.continuecard).visibility = View.GONE
            setpview5.visibility = View.GONE
        }
        stepsBeanList.add(stepBean0)
        stepsBeanList.add(stepBean1)
        stepsBeanList.add(stepBean2)
        stepsBeanList.add(stepBean3)
        setpview5.setStepViewTexts(stepsBeanList) //???
                .setTextSize(12) //set textSize
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(applicationContext, R.color.darkgrey)) //??StepsViewIndicator??????
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(applicationContext, android.R.color.darker_gray)) //??StepsViewIndicator???????
                .setStepViewComplectedTextColor(ContextCompat.getColor(applicationContext, R.color.darkgrey)) //??StepsView text??????
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(applicationContext, R.color.darkgrey)) //??StepsView text???????
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(applicationContext, R.drawable.checkedd)) //??StepsViewIndicator CompleteIcon
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(applicationContext, R.drawable.outline_radio_button_checked_24)) //??StepsViewIndicator DefaultIcon
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(applicationContext, R.drawable.uncheckedd))
        val query: Query = FirebaseDatabase.getInstance().reference.child("Profiles").child(FirebaseAuth.getInstance().currentUser.uid).child("Addresses")
        query.ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                recyclerView.scheduleLayoutAnimation()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        query.ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                progresslayout.setVisibility(View.GONE)
                continuetopay.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View) {
                        if (name != null) {
                            val intent = Intent(this@Address, OrderPaymentDetails::class.java)
                            intent.putExtra("name", name)
                            intent.putExtra("address", address)
                            intent.putExtra("phone", phone)
                            startActivity(intent)
                            CustomIntent.customType(this@Address, "left-to-right")
                        } else {
                            Sneaker.with(this@Address)
                                    .setTitle("Select An Address", R.color.white)
                                    .setMessage("Select existing or add a new address to continue", R.color.white)
                                    .setDuration(2000)
                                    .setIcon(R.drawable.delete2, R.color.white)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200)
                        }
                    }
                })
                if (!snapshot.exists()) {
                    noaddress.setVisibility(View.VISIBLE)
                    noaddresstext.setVisibility(View.VISIBLE)
                } else {
                    noaddress.setVisibility(View.GONE)
                    noaddresstext.setVisibility(View.GONE)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        val options = FirebaseRecyclerOptions.Builder<addressdetail>()
                .setQuery(query, object : SnapshotParser<addressdetail?> {
                    override fun parseSnapshot(snapshot: DataSnapshot): addressdetail {
                        return addressdetail(snapshot.child("name").getValue(String::class.java), snapshot.child("phone").getValue(String::class.java), snapshot.child("address").getValue(String::class.java), snapshot.child("city").getValue(String::class.java), snapshot.child("state").getValue(String::class.java), snapshot.child("pincode").getValue(String::class.java), snapshot.child("type").getValue(String::class.java))
                    }
                }).build()
        firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<addressdetail, AddressViewHolder>(options) {
            override fun onBindViewHolder(holder: AddressViewHolder, @SuppressLint("RecyclerView") position: Int, model: addressdetail) {
                if (posi == position) {
                    holder.check.setChecked(true, true)
                    name = firebaseRecyclerAdapter.getItem(position).name.toString()
                    phone = firebaseRecyclerAdapter.getItem(position).phone.toString()
                    address = firebaseRecyclerAdapter.getItem(position).address + ", " + firebaseRecyclerAdapter!!.getItem(position).city + ", " + firebaseRecyclerAdapter.getItem(position).state + ", " + firebaseRecyclerAdapter!!.getItem(position).pincode
                    Log.i("name", (name!!))
                    Log.i("phone", (phone))
                    Log.i("add", address)
                } else {
                    holder.check.setChecked(false, false)
                }
                if (intent.getStringExtra("pro") != null) {
                    holder.check.visibility = View.GONE
                }
                if (model.type != "") {
                    holder.type.text = model.type
                    holder.type.visibility = View.VISIBLE
                } else {
                    holder.type.visibility = View.GONE
                }
                holder.name.text = model.name
                holder.phone.text = model.phone
                holder.address.text = model.address + " " + model.city + " " + model.state + " " + model.pincode
                holder.itemView.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View) {
                        posi = position
                        firebaseRecyclerAdapter!!.notifyDataSetChanged()
                    }
                })
                holder.check.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View) {
                        posi = position
                        firebaseRecyclerAdapter!!.notifyDataSetChanged()
                    }
                })
                holder.delete.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View) {
                        val mDialog = MaterialDialog.Builder(this@Address)
                                .setTitle("Delete This Address?")
                                .setMessage("Remove this address permanently")
                                .setCancelable(false)
                                .setPositiveButton("Delete", R.drawable.delete2, object : AbstractDialog.OnClickListener {
                                    override fun onClick(dialogInterface: dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface, which: Int) {
                                        firebaseRecyclerAdapter!!.getRef(position).removeValue().addOnCompleteListener(object : OnCompleteListener<Void?> {
                                            override fun onComplete(task: Task<Void?>) {
                                                if (task.isSuccessful) {
                                                    dialogInterface.dismiss()
                                                    Sneaker.with(this@Address)
                                                            .setTitle("Address Deleted ", R.color.white)
                                                            .setMessage("Address Removed From The List", R.color.white)
                                                            .setDuration(2000)
                                                            .setIcon(R.drawable.info, R.color.white)
                                                            .autoHide(true)
                                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                            .setCornerRadius(10, 0)
                                                            .sneak(R.color.green)
                                                    firebaseRecyclerAdapter!!.notifyDataSetChanged()
                                                } else {
                                                    Sneaker.with(this@Address)
                                                            .setTitle("Error Delting Address ", R.color.white)
                                                            .setMessage("Please Try Again", R.color.white)
                                                            .setDuration(2000)
                                                            .setIcon(R.drawable.delete2, R.color.white)
                                                            .autoHide(true)
                                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                            .setCornerRadius(10, 0)
                                                            .sneak(R.color.teal_200)
                                                }
                                            }
                                        })
                                    }
                                })
                                .setNegativeButton("Cancel", R.drawable.cancel, object : AbstractDialog.OnClickListener {
                                    override fun onClick(dialogInterface: dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface, which: Int) {
                                        dialogInterface.dismiss()
                                    }
                                })
                                .build()
                        mDialog.show()
                    }
                })
                holder.edit.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View) {
                        run {
                            val name: EditText
                            val address: EditText
                            val city: EditText
                            val phone: EditText
                            val pincode: EditText
                            val state: TextView
                            val save: TextView
                            val home: TextView
                            val office: TextView
                            val cross: ImageView
                            val dialog: Dialog = Dialog(this@Address)
                            dialog.setContentView(R.layout.addaddress)
                            dialog.setCanceledOnTouchOutside(false)
                            dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            dialog.getWindow()!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                            dialog.getWindow()!!.getAttributes().windowAnimations = R.style.AppTheme_UpDown
                            name = dialog.findViewById(R.id.name)
                            address = dialog.findViewById(R.id.address)
                            city = dialog.findViewById(R.id.city)
                            state = dialog.findViewById(R.id.state)
                            save = dialog.findViewById(R.id.save)
                            phone = dialog.findViewById(R.id.phone)
                            pincode = dialog.findViewById(R.id.pincode)
                            cross = dialog.findViewById(R.id.cross)
                            home = dialog.findViewById(R.id.home)
                            office = dialog.findViewById(R.id.office)
                            name.setText(firebaseRecyclerAdapter!!.getItem(position).name)
                            address.setText(firebaseRecyclerAdapter!!.getItem(position).address)
                            if (firebaseRecyclerAdapter!!.getItem(position).type != null) {
                                if ((firebaseRecyclerAdapter!!.getItem(position).type == "Home")) {
                                    type = "Home"
                                    home.setTextColor(Color.WHITE)
                                    home.setBackgroundResource(R.drawable.stroke3)
                                } else if ((firebaseRecyclerAdapter!!.getItem(position).type == "Office")) {
                                    type = "Office"
                                    office.setTextColor(Color.WHITE)
                                    office.setBackgroundResource(R.drawable.stroke3)
                                }
                            }
                            city.setText(firebaseRecyclerAdapter.getItem(position).city)
                            state.setText(firebaseRecyclerAdapter.getItem(position).state)
                            phone.setText(firebaseRecyclerAdapter.getItem(position).phone)
                            pincode.setText(firebaseRecyclerAdapter.getItem(position).pincode)
                            home.setOnClickListener(object : View.OnClickListener {
                                override fun onClick(v: View) {
                                    type = "Home"
                                    home.setTextColor(Color.WHITE)
                                    home.setBackgroundResource(R.drawable.stroke3)
                                    office.setBackgroundResource(R.drawable.stroke2)
                                    office.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue))
                                }
                            })
                            office.setOnClickListener(object : View.OnClickListener {
                                override fun onClick(v: View) {
                                    type = "Office"
                                    office.setTextColor(Color.WHITE)
                                    office.setBackgroundResource(R.drawable.stroke3)
                                    home.setBackgroundResource(R.drawable.stroke2)
                                    home.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue))
                                }
                            })
                            cross.setOnClickListener(object : View.OnClickListener {
                                override fun onClick(view: View) {
                                    dialog.dismiss()
                                }
                            })
                            state.setKeyListener(null)
                            val listItems: Array<String> = arrayOf("Andaman and Nicobar Islands", "Andra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chandigarh", "Chhattisgarh", "Dadar and Nagar Haveli", "Daman and Diu", "Delhi", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala", "Ladakh", "Lakshadeep", "Madya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Orissa", "Punjab", "Pondicherry", "Rajasthan", "Sikkim", "Tamil Nadu", "Telagana", "Tripura", "Uttarakhand", "Uttar Pradesh", "West Bengal")
                            val builder: AlertDialog.Builder = AlertDialog.Builder(this@Address)
                            builder.setTitle("Your State")
                            builder.setItems(listItems, object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface, which: Int) {
                                    state.setText(listItems.get(which))
                                }
                            })
                            val dialog1: AlertDialog = builder.create()
                            state.setOnTouchListener(object : OnTouchListener {
                                override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                                    dialog1.show()
                                    return false
                                }
                            })
                            save.setOnClickListener(object : View.OnClickListener {
                                override fun onClick(view: View) {
                                    if ((name.getText().toString() == "")) {
                                        Toast.makeText(this@Address, "Enter name", Toast.LENGTH_SHORT).show()
                                    } else if ((phone.getText().toString() == "")) {
                                        Toast.makeText(this@Address, "Enter phone", Toast.LENGTH_SHORT).show()
                                    } else if (phone.getText().toString().length < 10) {
                                        Toast.makeText(this@Address, "Invalid Phone", Toast.LENGTH_SHORT).show()
                                    } else if ((address.getText().toString() == "")) {
                                        Toast.makeText(this@Address, "Enter house number", Toast.LENGTH_SHORT).show()
                                    } else if ((city.getText().toString() == "")) {
                                        Toast.makeText(this@Address, "Select city", Toast.LENGTH_SHORT).show()
                                    } else if ((state.getText().toString() == "")) {
                                        Toast.makeText(this@Address, "Enter state", Toast.LENGTH_SHORT).show()
                                    } else if ((pincode.getText().toString() == "")) {
                                        Toast.makeText(this@Address, "Enter pincode", Toast.LENGTH_SHORT).show()
                                    } else if (pincode.getText().toString().length < 6) {
                                        Toast.makeText(this@Address, "Invalid pincode", Toast.LENGTH_SHORT).show()
                                    } else {
                                        val alertDialog: android.app.AlertDialog = SpotsDialog.Builder()
                                                .setCancelable(false)
                                                .setContext(this@Address)
                                                .setTheme(R.style.ProgressDialog)
                                                .setMessage("Saving Address")
                                                .build()
                                        alertDialog.show()
                                        val map = mutableMapOf<String?, Any?>()
                                        map.put("name", name.getText().toString())
                                        map.put("phone", phone.getText().toString())
                                        map.put("address", address.getText().toString())
                                        map.put("city", city.getText().toString())
                                        map.put("state", state.getText().toString())
                                        map.put("pincode", pincode.getText().toString())
                                        map.put("type", type)
                                        firebaseRecyclerAdapter!!.getRef(position).updateChildren(map).addOnCompleteListener(object : OnCompleteListener<Void?> {
                                            override fun onComplete(task: Task<Void?>) {
                                                if (task.isSuccessful()) {
                                                    alertDialog.dismiss()
                                                    Sneaker.with(this@Address)
                                                            .setTitle("Changes Done", R.color.white)
                                                            .setMessage("Your changes made are saved", R.color.white)
                                                            .setDuration(2000)
                                                            .setIcon(R.drawable.info, R.color.white)
                                                            .autoHide(true)
                                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                            .setCornerRadius(10, 0)
                                                            .sneak(R.color.green)
                                                    dialog.dismiss()
                                                    firebaseRecyclerAdapter.notifyDataSetChanged()
                                                } else {
                                                    alertDialog.dismiss()
                                                    Sneaker.with(this@Address)
                                                            .setTitle("Error Saving Changes", R.color.white)
                                                            .setMessage("Please Try Again", R.color.white)
                                                            .setDuration(2000)
                                                            .setIcon(R.drawable.delete2, R.color.white)
                                                            .autoHide(true)
                                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                            .setCornerRadius(10, 0)
                                                            .sneak(R.color.teal_200)
                                                }
                                            }
                                        })
                                    }
                                }
                            })
                            dialog.show()
                        }
                    }
                })
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.addressitem, parent, false)
                return AddressViewHolder(view)
            }
        }
        recyclerView.setLayoutManager(LinearLayoutManager(this@Address))
        recyclerView.setAdapter(firebaseRecyclerAdapter)
    }

    override fun onResume() {
        super.onResume()
        firebaseRecyclerAdapter!!.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseRecyclerAdapter!!.stopListening()
    }

    inner class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var address: TextView
        var phone: TextView
        var delete: TextView
        var edit: TextView
        var type: TextView
        var check: CustomCheckBox

        init {
            name = itemView.findViewById(R.id.name)
            address = itemView.findViewById(R.id.address)
            phone = itemView.findViewById(R.id.phone)
            check = itemView.findViewById(R.id.check)
            delete = itemView.findViewById(R.id.delete)
            edit = itemView.findViewById(R.id.edit)
            type = itemView.findViewById(R.id.type)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        CustomIntent.customType(this@Address, "right-to-left")
    }
}