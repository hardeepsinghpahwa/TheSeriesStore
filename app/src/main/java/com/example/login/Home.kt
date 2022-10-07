package com.example.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.smarteist.autoimageslider.SliderView
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.login.Fragments.*
import java.util.ArrayList

class Home : AppCompatActivity() {


    @kotlin.jvm.JvmField
    var cat: String = ""
    @kotlin.jvm.JvmField
    var posi: Int = 0
    lateinit var items: ArrayList<SliderItem>
    lateinit var sliderView: SliderView
    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        val fragment: Fragment = HomeFragment()
        val fragmentManager: FragmentManager = getSupportFragmentManager()
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left)
                .replace(R.id.cons, fragment, "Home")
                .commit()
        bottomNavigationView.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener {
            public override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.getItemId()) {
                    R.id.page_1 -> {
                        val fragment: Fragment = HomeFragment()
                        val fragmentManager: FragmentManager = getSupportFragmentManager()
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left)
                                .replace(R.id.cons, fragment, "Home")
                                .commit()
                        return true
                    }
                    R.id.page_2 -> {
                        val fragment: Fragment = Categories()
                        val fragmentManager: FragmentManager = getSupportFragmentManager()
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left)
                                .replace(R.id.cons, fragment, "Categories")
                                .commit()
                        return true
                    }
                    R.id.page_3 -> {
                        val fragment: Fragment = SubCategories()
                        val fragmentManager: FragmentManager = getSupportFragmentManager()
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left)
                                .replace(R.id.cons, fragment, "SubCategories")
                                .commit()
                        return true
                    }
                    R.id.page_4 -> {
                        val fragment: Fragment = Profile()
                        val fragmentManager: FragmentManager = getSupportFragmentManager()
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left)
                                .replace(R.id.cons, fragment, "Profile")
                                .commit()
                        return true
                    }
                    else -> {
                        return false
                    }
                }
            }
        })
    }
}