package com.theateam.vitaflex

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.theateam.vitaflex.databinding.ActivitySignUpStep2Binding

class SignUpStep2Activity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpStep2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignUpStep2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setTheme(R.style.splash_screen)

        // setContentView(R.layout.activity_sign_up_step2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.step2goalsContinueBtn.setOnClickListener {
            val intent = Intent(this, SignUpStep3Activity::class.java)
            startActivity(intent)
        }
        binding.signUpStep2BackBtn.setOnClickListener {
            finish()
        }

        val weights = arrayListOf<Int>(40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105, 110, 115, 120);

        val arrayAdapterWeight = ArrayAdapter(this, android.R.layout.simple_spinner_item, weights)


        arrayAdapterWeight.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.step2goalsTargetWeightSpinner.adapter = arrayAdapterWeight

        binding.step2goalsTargetWeightSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                Toast.makeText(this@SignUpStep2Activity,"You have selected " + (weights[position]), Toast.LENGTH_SHORT).show()


            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }


        val frequency = arrayListOf<Int>(1, 2, 3, 4, 5, 6, 7);

        val arrayAdapterFrequency = ArrayAdapter(this, android.R.layout.simple_spinner_item, frequency)

        arrayAdapterFrequency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.step2goalsExerciseLevelSpinner.adapter = arrayAdapterFrequency

        binding.step2goalsExerciseLevelSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                Toast.makeText(this@SignUpStep2Activity,"You have selected " + (frequency[position]), Toast.LENGTH_SHORT).show()


            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }




        binding.step2goalsContinueBtn.setOnClickListener()
        {

            var weightLossGain: String

            if(binding.step2goalsWeightGoalGainRadioButton.isChecked)
            {
                weightLossGain = "Gain"
            }
            else
            {
                weightLossGain = "Lose"
            }

            val goalWeight = binding.step2goalsTargetWeightSpinner.selectedItem.toString().toInt()

            val exerciseFreq = binding.step2goalsExerciseLevelSpinner.selectedItem.toString().toInt()


            writeToFirebase(weightLossGain, goalWeight, exerciseFreq)


            val intent = Intent(this@SignUpStep2Activity,SignUpStep1Activity::class.java)
            startActivity(intent)
        }




    }



    private fun writeToFirebase(weightLossGain: String, weightGoal: Int, exerciseFreq: Int) {

        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)

        val email = userEmail?.replace(".", "").toString()

        val database = Firebase.database
        val dbRef = database.getReference("VitaflexApp")
        val entry = UserGoals(weightLossGain, weightGoal, exerciseFreq)

        dbRef.child("User Goals Entries").child(email).setValue(entry)
            .addOnSuccessListener {
                Log.d("CreateUserGoals", "Goals successfully recorded!")
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Database write failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("CreateUserGoals", "Database write failed", exception)
            }
    }


    data class UserGoals(val weightLossGain: String, val weightGoal: Int, val exerciseFreq:Int)
}