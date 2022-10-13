package com.example.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.login.Fragments.*
import com.example.login.databinding.ActivityHomeBinding
import com.example.login.dataclass.SliderItem
import java.util.ArrayList

class Home : AppCompatActivity() {


    @kotlin.jvm.JvmField
    var cat: String = ""

    @kotlin.jvm.JvmField
    var posi: Int = 0
    lateinit var items: ArrayList<SliderItem>
    lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.executePendingBindings()

        val fragment: Fragment = HomeFragment()
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left)
                .replace(R.id.cons, fragment, "Home")
                .commit()
        binding.bottomNavigation.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener {
            public override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.getItemId()) {
                    R.id.page_1 -> {
                        val fragment: Fragment = HomeFragment()
                        supportFragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left)
                                .replace(R.id.cons, fragment, "Home")
                                .commit()
                        return true
                    }
                    R.id.page_2 -> {
                        val fragment: Fragment = Categories()
                        supportFragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left)
                                .replace(R.id.cons, fragment, "Categories")
                                .commit()
                        return true
                    }
                    R.id.page_3 -> {
                        val fragment: Fragment = SubCategories()
                        supportFragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left)
                                .replace(R.id.cons, fragment, "SubCategories")
                                .commit()
                        return true
                    }
                    R.id.page_4 -> {
                        val fragment: Fragment = Profile()
                        supportFragmentManager.beginTransaction()
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