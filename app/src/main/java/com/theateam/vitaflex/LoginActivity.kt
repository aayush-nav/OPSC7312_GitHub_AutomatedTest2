package com.theateam.vitaflex

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theateam.vitaflex.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {


    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient


    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth

    private lateinit var googleEmail: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTheme(R.style.splash_screen)

        // setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.loginBackBtn.setOnClickListener {
            finish()
        }

        binding.loginScreenForgotPasswordBtn.setOnClickListener {

        }

        binding.loginScreenLoginBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.loginScreenSignInWithGoogleImageButton.setOnClickListener{

        }



//        googleSignInLauncher = registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//        ) { result ->
//            handleSignInResult(result)
//        }
//
//
//        val webclientid = "846530981193-oer7dnl613o3o3967ulqlll8j8p6bkft.apps.googleusercontent.com"
//
//
//        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(webclientid).requestEmail().build()
//        gsc = GoogleSignIn.getClient(this, gso)
//
//
//        //this will assign the value auth to allow authentication via firebase
//        auth = Firebase.auth
//
//        binding.loginScreenLoginBtn.setOnClickListener()
//        {
//            var email: String = binding.loginScreenEmailEditText.text.toString()
//            var password: String = binding.loginScreenPasswordEditTextPassword.text.toString()
//
//
//            //this will do data validation to ensure that all fields have data in them
//            if(binding.loginScreenEmailEditText.text.isEmpty() || binding.loginScreenPasswordEditTextPassword.text.isEmpty())
//            {
//                Toast.makeText(this,"Please ensure that all fields have been entered", Toast.LENGTH_SHORT).show()
//            }
//            else
//            {
//                login(email, password)
//            }
//        }
//
//
//
//        binding.loginScreenSignInWithGoogleImageButton.setOnClickListener()
//        {
//            loginGoogle()
//        }


    }



    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("USER_EMAIL", email)
                    editor.apply()

                    val googlePreferences = applicationContext.getSharedPreferences("GooglePref", Context.MODE_PRIVATE)
                    val googleEditor = googlePreferences.edit()
                    googleEditor.putBoolean("GOOGLE", true)
                    googleEditor.apply()


                    val userEmail = sharedPreferences.getString("USER_EMAIL", null)

                    Toast.makeText(this,"Log In Successful $userEmail", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@LoginActivity,MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this,"Log In Failed", Toast.LENGTH_LONG).show()
                }
            }
    }


    private fun loginGoogle() {
        // Sign out the user to ensure they can choose another account if desired
        gsc.signOut().addOnCompleteListener {
            val loginIntent = gsc.signInIntent
            googleSignInLauncher.launch(loginIntent)
        }


        val googlePreferences = applicationContext.getSharedPreferences("GooglePref", Context.MODE_PRIVATE)
        val googleEditor = googlePreferences.edit()
        googleEditor.putBoolean("GOOGLE", false)
        googleEditor.apply()
    }

    private fun handleSignInResult(result: ActivityResult) {
        val data = result.data
        if (result.resultCode == Activity.RESULT_OK && data != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                googleEmail = account.email.toString()

                if (account != null && account.idToken != null) {

                    // Check if this email is already registered in Firebase
                    checkIfUserExists(googleEmail)
                } else {
                    Toast.makeText(this, "Sign-in failed. Please try again. $googleEmail", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Log.w("Login with Google", "Google sign in failed", e)
            }
        } else {
            Log.w("Google Sign-In", "Sign-in failed or was canceled")
        }
    }

    private fun checkIfUserExists(email: String) {

        var arrUserEmailList = arrayListOf<String>()

        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("VitaflexApp")

        dbRef.child("Users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (userSnapshot in dataSnapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        if (user != null) {
                            Log.d("User", "User email: ${user.email}")
                            arrUserEmailList.add(user.email.toString())
                        } else {
                            Log.e("User", "User data is null for snapshot: ${userSnapshot.key}")
                        }
                    }

                    // Once data is retrieved, check if the email exists in the list
                    if (arrUserEmailList.contains(email)) {
                        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("USER_EMAIL", email)
                        editor.apply()

                        Toast.makeText(this@LoginActivity, "Log In Successful", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@LoginActivity, "Account does not exist", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("User", "Failed to get user data: ${error.message}")
                }
            })
    }


    data class User (var names:String? = null, var email:String? = null)
    {
        constructor() : this("","")
    }

}