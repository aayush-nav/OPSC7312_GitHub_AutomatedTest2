package com.theateam.vitaflex

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.theateam.vitaflex.databinding.ActivityListOfIngredientsBinding

class ListOfIngredientsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListOfIngredientsBinding

    private lateinit var ingredientsAdapter: IngredientsAdapter
    private var ingredientList: ArrayList<Ingredient> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityListOfIngredientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setContentView(R.layout.activity_list_of_ingredients)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Retrieve the ingredient list from the Intent
        ingredientList = intent.getSerializableExtra("ingredientList") as ArrayList<Ingredient>

        // Set up RecyclerView with an adapter
        ingredientsAdapter = IngredientsAdapter(ingredientList)
        binding.ingredientsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.ingredientsRecyclerView.adapter = ingredientsAdapter

        // Calculate total calories
        val totalCalories = ingredientList.sumByDouble { it.calories ?: 0.0 }
        binding.totalCaloriesTextView.text = "Total Calories: $totalCalories"

        // Button to go back and add more ingredients
        binding.addMoreIngredientsButton.setOnClickListener {
            finish() // Go back to the previous activity
        }

        // Next button to proceed to recipe info
        binding.nextButton.setOnClickListener {
            val intent = Intent(this, RecipeInfoActivity::class.java)
            intent.putExtra("ingredients", ingredientList)
            intent.putExtra("totalCalories", totalCalories)
            startActivity(intent)
        }

//        binding.listOfAllIngredientsBackButton.setOnClickListener() {
//            finish()
//        }


    }
}