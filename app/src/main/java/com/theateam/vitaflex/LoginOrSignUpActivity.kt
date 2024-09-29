package com.theateam.vitaflex

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.theateam.vitaflex.databinding.ActivityLoginOrSignUpBinding
import com.theateam.vitaflex.databinding.ActivityMainBinding
import com.theateam.vitaflex.databinding.ActivitySignUpStep1Binding

class LoginOrSignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginOrSignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginOrSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTheme(R.style.splash_screen)

        // setContentView(R.layout.activity_login_or_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.startUpScreenSignupBtn.setOnClickListener {
            val intent = Intent(this, SignUpStep3Activity::class.java)
            startActivity(intent)
        }

        binding.startUpScreenLoginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }



    }
}