package com.example.login.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.content.Intent
import maes.tech.intentanim.CustomIntent
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.bumptech.glide.Glide
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.login.*
import com.example.login.address.Address
import com.example.login.databinding.FragmentProfileBinding

class Profile : Fragment() {

    lateinit var binding: FragmentProfileBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_profile, container, false)
        binding.executePendingBindings()

        binding.address.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, Address::class.java)
            intent.putExtra("pro", "true")
            startActivity(intent)
            CustomIntent.customType(activity, "left-to-right")
        })
        binding.wishlist.setOnClickListener(View.OnClickListener {
            startActivity(Intent(activity, Wishlist::class.java))
            CustomIntent.customType(activity, "left-to-right")
        })

        FirebaseDatabase.getInstance().reference.child("Profiles").child(FirebaseAuth.getInstance().currentUser.uid).child("Cart").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    binding.cartcount.text = "" + snapshot.childrenCount
                } else {
                    binding.cartcount.text = "0"
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        binding.orders.setOnClickListener(View.OnClickListener {
            startActivity(Intent(activity, MyOrders::class.java))
            CustomIntent.customType(activity, "left-to-right")
        })
        binding.cart.setOnClickListener {
            startActivity(Intent(activity, Cart::class.java))
            CustomIntent.customType(activity, "bottom-to-up")
        }
        binding.profile.setOnClickListener(View.OnClickListener {
            startActivity(Intent(activity, EditProfile::class.java))
            CustomIntent.customType(activity, "left-to-right")
        })
        FirebaseDatabase.getInstance().reference.child("Profiles").child(FirebaseAuth.getInstance().currentUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.name.setText(snapshot.child("name").getValue(String::class.java))
                binding.email.setText(snapshot.child("email").getValue(String::class.java))
                binding.phone.setText("+91 " + snapshot.child("phone").getValue(String::class.java))
                if (snapshot.child("profilepic").exists()) {
                    Glide.with(activity!!).load(snapshot.child("profilepic").getValue(String::class.java)).into(binding.profilepic)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        return binding.root
    }
}