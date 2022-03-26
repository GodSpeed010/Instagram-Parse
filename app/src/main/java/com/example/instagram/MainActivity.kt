package com.example.instagram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.instagram.fragments.ComposeFragment
import com.example.instagram.fragments.HomeFragment
import com.example.instagram.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_nav)

        val fragmentManager: FragmentManager = supportFragmentManager

        bottomNav.setOnItemSelectedListener { item ->
            var fragment: Fragment? = null

            when (item.itemId) {
                R.id.action_home -> {
                    fragment = HomeFragment()
                }
                R.id.action_compose -> {
                    fragment = ComposeFragment()
                }
                R.id.action_profile -> {
                    fragment = ProfileFragment()
                }
            }

            if (fragment != null) {
                fragmentManager.beginTransaction().replace(R.id.fragment_container_view, fragment).commit()
            }

            //Return true to say that we've handled this user interaction
            true
        }

        //Set default fragment
        bottomNav.selectedItemId = R.id.action_home
    }

    companion object {
        const val TAG = "MainActivity"
    }
}