package com.theateam.vitaflex

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.theateam.vitaflex.databinding.ActivityMainBinding
import com.theateam.vitaflex.databinding.ActivitySignUpStep1Binding
import java.util.Calendar

class SignUpStep1Activity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpStep1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignUpStep1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setTheme(R.style.splash_screen)

        // setContentView(R.layout.activity_sign_up_step1)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val genders = arrayListOf<Char>('M', 'F');

        val arrayAdapterGenders = ArrayAdapter(this, android.R.layout.simple_spinner_item, genders)

//            arrayAdapter.notifyDataSetChanged()

        arrayAdapterGenders.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.signUpStep1GenderSelectSpinner.adapter = arrayAdapterGenders

        binding.signUpStep1GenderSelectSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                Toast.makeText(this@SignUpStep1Activity,"You have selected " + (genders[position]), Toast.LENGTH_SHORT).show()


            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }


        val weights = arrayListOf<Int>(40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105, 110, 115, 120);

        val arrayAdapterWeights = ArrayAdapter(this, android.R.layout.simple_spinner_item, weights)

//            arrayAdapter.notifyDataSetChanged()

        arrayAdapterWeights.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.signUpStep1WeightSelectSpinner.adapter = arrayAdapterWeights

        binding.signUpStep1WeightSelectSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                Toast.makeText(this@SignUpStep1Activity,"You have selected " + (weights[position]), Toast.LENGTH_SHORT).show()


            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }




        val heights = arrayListOf<Int>(140, 145, 150, 155, 160, 165, 170, 175, 180, 185, 190, 195, 200, 205, 210, 215, 220);

        val arrayAdapterHeights = ArrayAdapter(this, android.R.layout.simple_spinner_item, heights)

//            arrayAdapter.notifyDataSetChanged()

        arrayAdapterHeights.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.signUpStep1HeightSelectSpinner.adapter = arrayAdapterHeights

        binding.signUpStep1HeightSelectSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                Toast.makeText(this@SignUpStep1Activity,"You have selected " + (heights[position]), Toast.LENGTH_SHORT).show()


            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }



        binding.signUpStep1DateOfBirthEditText.setOnClickListener()
        {
            showDatePicker(binding.signUpStep1DateOfBirthEditText)
        }


        binding.signUpStep1ContinueBtn.setOnClickListener()
        {
            val gender = binding.signUpStep1GenderSelectSpinner.selectedItem.toString()
            val dob = binding.signUpStep1DateOfBirthEditText.text.toString()
            val weight = binding.signUpStep1WeightSelectSpinner.selectedItem.toString().toInt()
            val height = binding.signUpStep1HeightSelectSpinner.selectedItem.toString().toInt()

            writeToFirebase(gender, dob, weight, height)


            val intent = Intent(this@SignUpStep1Activity,MainActivity::class.java)
            startActivity(intent)

        }


    }


    private fun writeToFirebase(gender: String, dob: String, weight: Int, height: Int) {

        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)

        val email = userEmail?.replace(".", "").toString()

        val database = Firebase.database
        val dbRef = database.getReference("VitaflexApp")
        val entry = UserPersonalInfo(gender, dob, weight, height)

        dbRef.child("User Personal Info Entries").child(email).setValue(entry)
            .addOnSuccessListener {
                Log.d("CreateUserPInfo", "Personal Info successfully recorded!")
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Database write failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("CreateUserPInfo", "Database write failed", exception)
            }
    }


    data class UserPersonalInfo(val gender: String, val dob: String, val weight: Int, val height: Int)



    private fun showDatePicker(editText: EditText) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Fetch the week start day from settings
//        val weekStartDay = when (SettingsUtil.getWeekStart(this)) {
//            "Monday" -> Calendar.MONDAY
//            "Sunday" -> Calendar.SUNDAY
//            else -> Calendar.SUNDAY
//        }

        // Set the first day of the week in the calendar
        //c.firstDayOfWeek = weekStartDay

        val datePickerDialog = DatePickerDialog(this, { _, theYear, monthOfYear, dayOfMonth ->
            val formattedDate = "$dayOfMonth-${monthOfYear + 1}-$theYear"
            editText.setText(formattedDate)
        }, year, month, day)

        // Set the first day of the week in the DatePickerDialog
        //datePickerDialog.datePicker.firstDayOfWeek = weekStartDay

        datePickerDialog.show()
        }

}