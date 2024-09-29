package com.theateam.vitaflex

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theateam.vitaflex.databinding.ActivityProfileSettingBinding
import java.math.BigDecimal
import java.math.RoundingMode

class ProfileSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSettingBinding

    private lateinit var btnChangeAge: ImageButton
    private lateinit var btnChangeGender: ImageButton
    private lateinit var btnChangeWeight: ImageButton
    private lateinit var btnChangeHeight: ImageButton
    private lateinit var txtBMI: TextView
    private lateinit var txtAge: TextView
    private lateinit var txtGender: TextView
    private lateinit var txtWeight: TextView
    private lateinit var txtHeight: TextView



    private var BMI: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProfileSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setContentView(R.layout.activity_profile_setting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        btnChangeAge = findViewById(R.id.imbChangeAge)
        btnChangeGender = findViewById(R.id.imbChangeGender)
        btnChangeWeight = findViewById(R.id.imbChangeWeight)
        btnChangeHeight = findViewById(R.id.imbChangeHeight)
        txtBMI = findViewById(R.id.txvProfileUserBMI)
        txtAge = findViewById(R.id.txvProfileUserAge)
        txtGender = findViewById(R.id.txvProfileUserGender)
        txtWeight = findViewById(R.id.txvProfileUserWeight)
        txtHeight = findViewById(R.id.txvProfileUserHeight)


        binding.ProfileBackBtn.setOnClickListener() {
            finish()
        }

        txtBMI = findViewById(R.id.txvProfileUserBMI)



        getAge()
        getGender()
        getWeight()
        getHeight()
        getBMI()


        btnChangeAge.setOnClickListener()
        {
            popUpScreen("Age", "Age", this)
            {
                    input ->
                if(input.isEmpty())
                {
                    Toast.makeText(this,"Please enter an age", Toast.LENGTH_LONG).show()
                }
                else
                {
                    updateAge(input.toInt())
                }
            }
        }

        btnChangeGender.setOnClickListener()
        {
            popUpScreen("Gender", "Gender", this)
            {
                    input ->
                if(input.isEmpty())
                {
                    Toast.makeText(this,"Please enter a gender", Toast.LENGTH_LONG).show()
                }
                else
                {
                    updateGender(input)
                }
            }
        }

        btnChangeWeight.setOnClickListener()
        {
            popUpScreen("Weight", "Weight", this)
            {
                    input ->
                if(input.isEmpty())
                {
                    Toast.makeText(this,"Please enter a weight", Toast.LENGTH_LONG).show()
                }
                else
                {
                    updateWeight(input.toInt())
                }
            }
        }

        btnChangeHeight.setOnClickListener()
        {
            popUpScreen("Height", "Height", this)
            {
                    input ->
                if(input.isEmpty())
                {
                    Toast.makeText(this,"Please enter a height", Toast.LENGTH_LONG).show()
                }
                else
                {
                    updateHeight(input.toInt())
                }
            }
        }





    }



    private fun updateHeight(newHeight: Int) {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("VitaflexApp")

        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)
        val checkUserEmail = userEmail?.replace(".", "").toString()

        val updateMap = mapOf<String, Any>(
            "height" to newHeight // Specify the field to update
        )

        dbRef.child("User Personal Info Entries").child(checkUserEmail).updateChildren(updateMap)
            .addOnSuccessListener {
                Log.d("UpdateUserName", "User's height successfully updated!")
                Toast.makeText(this, "Height updated successfully", Toast.LENGTH_SHORT).show()

                getBMI()
                getHeight()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Height update failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("UpdateUserName", "Failed to update height", exception)
            }
    }

    private fun updateWeight(newWeight: Int) {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("VitaflexApp")

        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)
        val checkUserEmail = userEmail?.replace(".", "").toString()

        val updateMap = mapOf<String, Any>(
            "weight" to newWeight // Specify the field to update
        )

        dbRef.child("User Personal Info Entries").child(checkUserEmail).updateChildren(updateMap)
            .addOnSuccessListener {
                Log.d("UpdateUserName", "User's weight successfully updated!")
                Toast.makeText(this, "Weight updated successfully", Toast.LENGTH_SHORT).show()

                getBMI()
                getWeight()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Weight update failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("UpdateUserName", "Failed to update weight", exception)
            }
    }

    private fun updateGender(newGender: String) {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("VitaflexApp")

        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)
        val checkUserEmail = userEmail?.replace(".", "").toString()

        val updateMap = mapOf<String, Any>(
            "gender" to newGender // Specify the field to update
        )

        dbRef.child("User Personal Info Entries").child(checkUserEmail).updateChildren(updateMap)
            .addOnSuccessListener {
                Log.d("UpdateUserName", "User's gender successfully updated!")
                Toast.makeText(this, "Gender updated successfully", Toast.LENGTH_SHORT).show()

                getGender()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gender update failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("UpdateUserName", "Failed to update gender", exception)
            }
    }

    private fun updateAge(newAge:Int) {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("VitaflexApp")

        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)
        val checkUserEmail = userEmail?.replace(".", "").toString()

        val updateMap = mapOf<String, Any>(
            "age" to newAge // Specify the field to update
        )

        dbRef.child("User Personal Info Entries").child(checkUserEmail).updateChildren(updateMap)
            .addOnSuccessListener {
                Log.d("UpdateUserName", "User's age successfully updated!")
                Toast.makeText(this, "Age updated successfully", Toast.LENGTH_SHORT).show()


                getAge()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Age update failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("UpdateUserName", "Failed to update age", exception)
            }
    }

    private fun getBMI() {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("VitaflexApp")

        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)
        val checkUserEmail = userEmail?.replace(".", "").toString()


        var weight = 0.0
        var heightInMetres = 0.0
        var calcHeight = 0.0

        dbRef.child("User Personal Info Entries").child(checkUserEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(UserInfo::class.java)
                    if (user != null) {

                        weight = user.weight.toString().toDouble()
                        heightInMetres = user.height.toString().toDouble()/100

                        calcHeight = heightInMetres * heightInMetres

                        BMI = weight/calcHeight

                        val roundedBMI = BigDecimal(BMI).setScale(2, RoundingMode.HALF_UP).toDouble()


                        txtBMI.setText("$roundedBMI")


                        Log.d("UserInfo", "User info: $user")
                    } else {
                        Log.e("UserInfo", "No data found for key: $checkUserEmail")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("UserInfo", "Failed to get data: ${error.message}")
                }
            })



    }

    private fun getAge() {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("VitaflexApp")

        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)
        val checkUserEmail = userEmail?.replace(".", "").toString()

        dbRef.child("User Personal Info Entries").child(checkUserEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(UserInfo::class.java)
                    if (user != null) {

                        txtAge.setText("${user.age}")


                        Log.d("UserInfo", "User info: $user")
                    } else {
                        Log.e("UserInfo", "No data found for key: $checkUserEmail")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("UserInfo", "Failed to get data: ${error.message}")
                }
            })
    }



    private fun getGender() {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("VitaflexApp")

        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)
        val checkUserEmail = userEmail?.replace(".", "").toString()

        dbRef.child("User Personal Info Entries").child(checkUserEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(UserInfo::class.java)
                    if (user != null) {

                        txtGender.setText("${user.gender}")


                        Log.d("UserInfo", "User info: $user")
                    } else {
                        Log.e("UserInfo", "No data found for key: $checkUserEmail")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("UserInfo", "Failed to get data: ${error.message}")
                }
            })
    }


    private fun getWeight() {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("VitaflexApp")

        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)
        val checkUserEmail = userEmail?.replace(".", "").toString()

        dbRef.child("User Personal Info Entries").child(checkUserEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(UserInfo::class.java)
                    if (user != null) {

                        txtWeight.setText("${user.weight}")


                        Log.d("UserInfo", "User info: $user")
                    } else {
                        Log.e("UserInfo", "No data found for key: $checkUserEmail")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("UserInfo", "Failed to get data: ${error.message}")
                }
            })
    }



    private fun getHeight() {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("VitaflexApp")

        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)
        val checkUserEmail = userEmail?.replace(".", "").toString()

        dbRef.child("User Personal Info Entries").child(checkUserEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(UserInfo::class.java)
                    if (user != null) {

                        txtHeight.setText("${user.height}")


                        Log.d("UserInfo", "User info: $user")
                    } else {
                        Log.e("UserInfo", "No data found for key: $checkUserEmail")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("UserInfo", "Failed to get data: ${error.message}")
                }
            })
    }


    //Author: Aaron
    //Source: https://stackoverflow.com/questions/10903754/input-text-dialog-android
    //this will show a popup screen that will allow the user to enter an email to reset their password
    private fun popUpScreen(forgotString: String, enterString: String, context: Context, onInputListener: (input: String) -> Unit) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Changing $forgotString?")
        alertDialogBuilder.setMessage("Enter your $enterString")

        val inputEditText = EditText(context)
        alertDialogBuilder.setView(inputEditText)

        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            val userInput = inputEditText.text.toString()
            onInputListener(userInput)
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }



    data class UserInfo (var age:Int? = null, var gender:String? = null, var height:Int? = null, var weight:Int? = null)
    {
        constructor() : this(0, "",0,0,)
    }

}