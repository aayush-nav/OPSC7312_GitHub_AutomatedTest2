package com.theateam.vitaflex

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.theateam.vitaflex.databinding.ActivityAddNewMealBinding

class AddNewMealActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewMealBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddNewMealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setContentView(R.layout.activity_add_new_meal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        // Set up the ViewPager with the adapter
        val adapter = NewMealsPagerAdapter(this)
        viewPager.adapter = adapter

        // Attach TabLayout to ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Existing Recipes"
                1 -> tab.text = "Other Food Items"
            }
        }.attach()
    }
}