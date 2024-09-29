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
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.theateam.vitaflex.databinding.ActivitySignUpStep3Binding

class SignUpStep3Activity : AppCompatActivity() {

    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient


    private lateinit var binding: ActivitySignUpStep3Binding


    //authentication variable used for firebase user authentication
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignUpStep3Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setTheme(R.style.splash_screen)

        // setContentView(R.layout.activity_sign_up_step3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            handleSignInResult(result)
        }


        val webclientid = "846530981193-oer7dnl613o3o3967ulqlll8j8p6bkft.apps.googleusercontent.com"


        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webclientid).requestEmail().build()
        gsc = GoogleSignIn.getClient(this, gso)


        //this will assign the value auth to allow authentication via firebase
        auth = Firebase.auth

        //find source
        val specialCharsRegex = Regex("[^a-zA-Z0-9]")





        binding.signUpStep3BackBtn.setOnClickListener {
            finish()
        }

        binding.finalStepSignUpSignUpBtn.setOnClickListener {
            var name: String = binding.fullNameStepSignUpFullNameEditText.text.toString()
            var email: String = binding.finalStepSignUpEmailEditTextEmailAddress.text.toString()
            var password: String = binding.finalStepSignUpPasswordEditTextPassword.text.toString()
            var confirmPass: String =
                binding.finalStepSignUpConfirmPasswordEditTextPassword.text.toString()


            //this will do data validation to ensure that all fields have data in them
            if (binding.fullNameStepSignUpFullNameEditText.text.isEmpty() || binding.finalStepSignUpEmailEditTextEmailAddress.text.isEmpty()
                || binding.finalStepSignUpPasswordEditTextPassword.text.isEmpty() || binding.finalStepSignUpConfirmPasswordEditTextPassword.text.isEmpty()
                || !binding.finalStepSignUpAgreeToTCCheckbox.isChecked
            ) {
                Toast.makeText(
                    this,
                    "Please ensure that all fields have been entered",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (password.length < 12 || !specialCharsRegex.containsMatchIn(password)) {
                Toast.makeText(
                    this,
                    "Please ensure that the password is at least 12 characters and includes special characters",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                //this will make sure that the password and password confirmation are matching each other
                if (password != confirmPass) {
                    Toast.makeText(this, "Error, the passwords do not match", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    //if all values are okay, the sign up method will run
                    signUp(name, email, password)
                }

            }

            binding.finalStepSignUpSignInWithGoogleImageButton.setOnClickListener()
            {
                signUpGoogle()
            }

        }
    }



    private fun signUpGoogle() {

        gsc.signOut().addOnCompleteListener {
            val signUpIntent = gsc.signInIntent
            googleSignInLauncher.launch(signUpIntent)
        }

        val googlePreferences = applicationContext.getSharedPreferences("GooglePref", Context.MODE_PRIVATE)
        val googleEditor = googlePreferences.edit()
        googleEditor.putBoolean("GOOGLE", false)
        googleEditor.apply()
    }

    //find a source
    private fun handleSignInResult(result: ActivityResult) {
        val data = result.data
        if (result.resultCode == Activity.RESULT_OK && data != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                // Check if account or idToken is null
                if (account != null && account.idToken != null) {
                    Log.d("Create User with Google", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                }
                else if (account == null)
                {
                    Log.w("Create User with Google", "Google sign in failed: Null account")
                    Toast.makeText(this, "Sign-in failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
                else if (account.idToken == null)
                {
                    Log.w("Create User with Google", "Google sign in failed: Null ID Token")
                    Toast.makeText(this, "Sign-in failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Log.w("Create User with Google", "Google sign in failed", e)
            }
        } else {
            Log.w("Google Sign-In", "Sign-in failed or was canceled")
        }
    }

    //find source for this
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Create User with Google", "signInWithCredential:success")
                    val user = auth.currentUser
                    val googleName = user?.displayName.toString()
                    val googleEmail = user?.email.toString()

                    writeToFirebase(googleName, googleEmail)

                    Toast.makeText(this, "Authentication Successful.", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@SignUpStep3Activity, SignUpStep2Activity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Log.w("Create User with Google", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }


    //this method will use the firebase create user method to make a new user using email and password
    private fun signUp(userName: String, userEmail: String, userPassword: String) {
        auth.createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    writeToFirebase(userName, userEmail)


                    val googlePreferences = applicationContext.getSharedPreferences("GooglePref", Context.MODE_PRIVATE)
                    val googleEditor = googlePreferences.edit()
                    googleEditor.putBoolean("GOOGLE", true)
                    googleEditor.apply()


                    Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@SignUpStep3Activity, SignUpStep2Activity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Sign Up Failed", Toast.LENGTH_LONG).show()
                }
            }
    }


    private fun writeToFirebase(names: String, email: String) {
        val database = Firebase.database
        val dbRef = database.getReference("VitaflexApp")
        val entry = User(names, email)

        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("USER_EMAIL", email)
        editor.apply()


        val passEmail = email.replace(".", "")

        dbRef.child("Users").child(passEmail).setValue(entry)
            .addOnSuccessListener {
                Log.d("CreateUser", "User successfully stored!")
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Database write failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("CreateUser", "Database write failed", exception)
            }
    }


    data class User(val names: String, val email:String)

}


