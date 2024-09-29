package com.theateam.vitaflex

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.theateam.vitaflex.databinding.ActivityRecipeDetailBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailBinding

    private lateinit var ingredientAdapter: IngredientsAdapter
    private var selectedDate: String? = null

    private var meal = ArrayList<Meal>()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setContentView(R.layout.activity_recipe_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Get the recipe from intent
        val recipe = intent.getSerializableExtra("recipe") as Recipe
        val boolAddToMeal = intent.getBooleanExtra("boolAddToMeal", false)

        if (boolAddToMeal) {
            binding.addMealButton.visibility = View.VISIBLE
        }

        // Set recipe details
        binding.recipeNameTextView.text = recipe.name


        binding.totalCaloriesTextView.text = "Total Calories: ${recipe.totalCalories}"

        // Set up ingredients RecyclerView
        ingredientAdapter = IngredientsAdapter(recipe.ingredients)
        binding.ingredientsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.ingredientsRecyclerView.adapter = ingredientAdapter

        binding.recipeDetailsBackButton.setOnClickListener() {
            finish()
        }

        binding.addMealButton.setOnClickListener() {
//            val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
//            val userEmail = sharedPreferences.getString("USER_EMAIL", null)
            val userEmail = "user@example.com"

            selectedDate = getSelectedDate() // Retrieve the saved date
            Log.d("NextActivity", "Selected date: $selectedDate")

            val formattedDate = convertDate(selectedDate!!)
            Log.d("NextActivity", "Formatted date: $formattedDate")

            val sharedPreferencesMeals = getSharedPreferences("MyMealCategoryPreferences", MODE_PRIVATE)
            val mealCategory = sharedPreferencesMeals.getString("mealCategory", null)

            Log.d("NextActivity", "Meal category: $mealCategory")

            val mealItem = MealType(recipe.name, recipe.totalCalories!!)
            //meal.add(Meal(userEmail!!, selectedDate!!, mealCategory!!, mealItem))

            saveMeal(userEmail, formattedDate, mealCategory!!, mealItem)




        }


    }


    fun convertDate(inputDate: String): String {
        // Define the formatter for the input date (e.g., "25 sep 2024")
        val inputFormatter = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)

        // Parse the input date string to Date
        val date = inputFormatter.parse(inputDate)

        // Define the formatter for the output date (e.g., "2024-09-25")
        val outputFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

        // Format the date and return it
        return outputFormatter.format(date)
    }



    private fun addMeal(meal: Meal){

        val gson = Gson()
        val mealJson = gson.toJson(meal)

        Log.d("JSON_REQUEST", mealJson) // Log the JSON request
        ApiClient.apiService.addMeal(meal).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RecipeDetailActivity, "Recipe added successfully", Toast.LENGTH_SHORT).show()
                    Log.d("API_SUCCESS", "Recipe added successfully: ${response.body()?.string()}")
                } else {
                    Toast.makeText(this@RecipeDetailActivity, "Failed to add recipe", Toast.LENGTH_SHORT).show()
                    Log.e("API_ERROR", "Error: ${response.code()}, ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("API_FAILURE", "Failed to make request: ${t.message}")
                Toast.makeText(this@RecipeDetailActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun saveMeal(email: String, date: String, mealCategory: String, mealItem: MealType) {
        // Create a list of meal items (you can add more items to this list as needed)
        //val mealItems = listOf(mealItem)
        //val arrayMeal = ArrayList<MealType>()

       // val mealItem = MealType(recipe.name, recipe.totalCalories!!)
        val mealItems = listOf(mealItem) // This should always be a list

// Then send this list to the API
        val meal = Meal(email, date, mealCategory, mealItems)

        //arrayMeal.add(mealItem)

        // Create a Meal object with the list of meal items
        //val meal = Meal(email, date, mealCategory, arrayMeal)

        // Send the meal to the API
        addMeal(meal)
        Log.d("RecipeDetailActivity", "Meal added: $meal")
    }



    private fun getSelectedDate(): String? {
        val sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        return sharedPreferences.getString("selectedDate", null)
    }





}