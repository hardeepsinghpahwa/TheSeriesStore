package com.example.login.Fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.login.Address;
import com.example.login.Cart;
import com.example.login.EditProfile;
import com.example.login.MyOrders;
import com.example.login.R;
import com.example.login.Wishlist;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static maes.tech.intentanim.CustomIntent.customType;

public class Profile extends Fragment {

    public Profile() {
        // Required empty public constructor
    }


    CardView profile,orders,wishlist,address;
    TextView name,phone,email;
    ImageView profilepic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);

        profile=view.findViewById(R.id.profile);
        orders=view.findViewById(R.id.orders);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        profilepic = view.findViewById(R.id.profilepic);
        phone = view.findViewById(R.id.phone);
        wishlist=view.findViewById(R.id.wishlist);
        address=view.findViewById(R.id.address);

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=(new Intent(getActivity(), Address.class));
                intent.putExtra("pro","true");
                startActivity(intent);
                customType(getActivity(),"left-to-right");
            }
        });

        wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Wishlist.class));
                customType(getActivity(),"left-to-right");
            }
        });

        TextView cartcount;
        cartcount=view.findViewById(R.id.cartcount);

        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    cartcount.setText(""+snapshot.getChildrenCount());
                }
                else {
                    cartcount.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyOrders.class));
                customType(getActivity(),"left-to-right");
            }
        });

        view.findViewById(R.id.cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Cart.class));
                customType(getActivity(),"bottom-to-up");
            }
        });


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfile.class));
                customType(getActivity(),"left-to-right");
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText(snapshot.child("name").getValue(String.class));
                email.setText(snapshot.child("email").getValue(String.class));
                phone.setText("+91 "+snapshot.child("phone").getValue(String.class));

                if (snapshot.child("profilepic").exists()) {
                    Glide.with(getActivity()).load(snapshot.child("profilepic").getValue(String.class)).into(profilepic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }
}

