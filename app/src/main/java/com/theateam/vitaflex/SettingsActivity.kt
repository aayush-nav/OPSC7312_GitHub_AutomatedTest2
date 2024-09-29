package com.theateam.vitaflex

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.theateam.vitaflex.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.SettingAppSettingsImageButton.setOnClickListener {
            val intent = Intent(this, AppSettingActivity::class.java)
            startActivity(intent)
        }

        binding.SettingsAccountImageButton.setOnClickListener() {
            val intent = Intent(this, AccountSettingActivity::class.java)
            startActivity(intent)
        }

        binding.SettingsProfileImageButton.setOnClickListener() {
            val intent = Intent(this, ProfileSettingActivity::class.java)
            startActivity(intent)
        }

        binding.SettingsSupportImageButton.setOnClickListener() {
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
        }

        binding.SettingsAboutImageButton.setOnClickListener() {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }

    }
}