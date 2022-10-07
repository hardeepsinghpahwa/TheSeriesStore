package com.example.login.Fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.content.Intent
import maes.tech.intentanim.CustomIntent
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.bumptech.glide.Glide
import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.login.*

class Profile : Fragment() {
    lateinit var profile: CardView
    lateinit var orders: CardView
    lateinit var wishlist: CardView
    lateinit var address: CardView
    lateinit var name: TextView
    lateinit var phone: TextView
    lateinit var email: TextView
    lateinit var profilepic: ImageView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        profile = view.findViewById(R.id.profile)
        orders = view.findViewById(R.id.orders)
        name = view.findViewById(R.id.name)
        email = view.findViewById(R.id.email)
        profilepic = view.findViewById(R.id.profilepic)
        phone = view.findViewById(R.id.phone)
        wishlist = view.findViewById(R.id.wishlist)
        address = view.findViewById(R.id.address)
        address.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, Address::class.java)
            intent.putExtra("pro", "true")
            startActivity(intent)
            CustomIntent.customType(activity, "left-to-right")
        })
        wishlist.setOnClickListener(View.OnClickListener {
            startActivity(Intent(activity, Wishlist::class.java))
            CustomIntent.customType(activity, "left-to-right")
        })
        val cartcount: TextView
        cartcount = view.findViewById(R.id.cartcount)
        FirebaseDatabase.getInstance().reference.child("Profiles").child(FirebaseAuth.getInstance().currentUser.uid).child("Cart").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    cartcount.text = "" + snapshot.childrenCount
                } else {
                    cartcount.text = "0"
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        orders.setOnClickListener(View.OnClickListener {
            startActivity(Intent(activity, MyOrders::class.java))
            CustomIntent.customType(activity, "left-to-right")
        })
        view.findViewById<View>(R.id.cart).setOnClickListener {
            startActivity(Intent(activity, Cart::class.java))
            CustomIntent.customType(activity, "bottom-to-up")
        }
        profile.setOnClickListener(View.OnClickListener {
            startActivity(Intent(activity, EditProfile::class.java))
            CustomIntent.customType(activity, "left-to-right")
        })
        FirebaseDatabase.getInstance().reference.child("Profiles").child(FirebaseAuth.getInstance().currentUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                name.setText(snapshot.child("name").getValue(String::class.java))
                email.setText(snapshot.child("email").getValue(String::class.java))
                phone.setText("+91 " + snapshot.child("phone").getValue(String::class.java))
                if (snapshot.child("profilepic").exists()) {
                    Glide.with(activity!!).load(snapshot.child("profilepic").getValue(String::class.java)).into(profilepic)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        return view
    }
}