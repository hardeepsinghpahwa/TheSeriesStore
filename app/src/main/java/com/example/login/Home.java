package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.login.Fragments.Categories;
import com.example.login.Fragments.HomeFragment;
import com.example.login.Fragments.Profile;
import com.example.login.Fragments.SubCategories;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {


    ArrayList<SliderItem> items;
    SliderView sliderView;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView=findViewById(R.id.bottom_navigation);

        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left)
                .replace(R.id.cons, fragment, "Home")
                .commit();


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.page_1: {
                        Fragment fragment = new HomeFragment();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left)
                                .replace(R.id.cons, fragment, "Home")
                                .commit();
                        return true;
                    }
                    case R.id.page_2: {
                        Fragment fragment = new Categories();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left)
                                .replace(R.id.cons, fragment, "Categories")
                                .commit();
                        return true;
                    }
                    case R.id.page_3: {
                        Fragment fragment = new SubCategories();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left)
                                .replace(R.id.cons, fragment, "SubCategories")
                                .commit();
                        return true;
                    }
                    case R.id.page_4: {
                        Fragment fragment = new Profile();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left)
                                .replace(R.id.cons, fragment, "Profile")
                                .commit();
                        return true;
                    }
                    default:{
                        return false;
                    }

                }

            }
        });

    }


}