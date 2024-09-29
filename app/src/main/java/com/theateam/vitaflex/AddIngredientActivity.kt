package com.theateam.vitaflex

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.theateam.vitaflex.databinding.ActivityAddIngredientBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddIngredientActivity : AppCompatActivity() {

    private val API_BASE_URL = "https://api.calorieninjas.com/"
    private val API_KEY = "24Dq75ydbIBCI6KpY2+BeQ==SJTYu7ebGCugjYj5" // Add your CaloriesNinjas API key here

    private lateinit var binding: ActivityAddIngredientBinding

    private var ingredientList: ArrayList<Ingredient> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddIngredientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setContentView(R.layout.activity_add_ingredient)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(CaloriesNinjasApi::class.java)

        var currentCalories: Double? = null
        var currentProtein: Double? = null
        var currentCarbs: Double? = null
        var currentCholesterol: Double? = null
        var currentFatTotal: Double? = null
        var currentFiber: Double? = null
        var currentSugar: Double? = null

        binding.searchButton.setOnClickListener() {
            val query = binding.searchBar.text.toString()

            if (query.isNotEmpty()) {
                // Make the API call
                val call = api.searchFood(API_KEY, query)

                call?.enqueue(object : Callback<NutritionResponse?> {
                    override fun onFailure(call: Call<NutritionResponse?>, t: Throwable) {
                        // Handle error
                        Log.e("API_ERROR", t.message ?: "Unknown error")
                        binding.nutritionInfo.text = "Error fetching data."
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onResponse(
                        call: Call<NutritionResponse?>,
                        response: Response<NutritionResponse?>
                    ) {
                        if (response.isSuccessful) {
                            val nutritionResponse = response.body()
                            if (nutritionResponse != null && nutritionResponse.items!!.isNotEmpty()) {
                                val firstItem = nutritionResponse.items!![0]

                                currentCalories = firstItem.calories ?: 0.0
                                currentProtein = firstItem.protein_g ?: 0.0
                                currentCarbs = firstItem.carbohydrates_total_g ?: 0.0
                                currentCholesterol = firstItem.cholesterol_mg ?: 0.0
                                currentFatTotal = firstItem.fat_total_g ?: 0.0
                                currentFiber = firstItem.fiber_g ?: 0.0
                                currentSugar = firstItem.sugar_g ?: 0.0

                                // Display the nutritional info in the TextView
                                binding.nutritionInfo.text = """
                                                    Food: ${firstItem.name}
                                                    Calories: ${firstItem.calories}
                                                    Protein: ${firstItem.protein_g}g
                                                    Carbs: ${firstItem.carbohydrates_total_g}g
                                                    Cholestrol: ${firstItem.cholesterol_mg}mg
                                                    Fat Total: ${firstItem.fat_total_g}g
                                                    Fiber: ${firstItem.fiber_g}g
                                                    Sugar: ${firstItem.sugar_g}g
                                                """.trimIndent()
                                binding.addIngredientButton.isEnabled = true
                            } else {
                                binding.nutritionInfo.text = "No nutrition data available for '$query'."
                            }
                        } else {
                            binding.nutritionInfo.text = "Failed to get data from API."
                        }
                    }
                })
            } else {
                binding.nutritionInfo.text = "Please enter an ingredient."
            }
        }


        binding.addIngredientButton.setOnClickListener {
            val ingredientName = binding.searchBar.text.toString()
            val ingredientCalories = currentCalories
            val ingredientProtein = currentProtein
            val ingredientCarbs = currentCarbs
            val ingredientCholesterol = currentCholesterol
            val ingredientFatTotal = currentFatTotal
            val ingredientFiber = currentFiber
            val ingredientSugar = currentSugar

            val ingredient = Ingredient(ingredientName, ingredientCalories, ingredientProtein, ingredientCarbs,
                ingredientCholesterol, ingredientFatTotal, ingredientFiber, ingredientSugar)
            ingredientList.add(ingredient)

            // Navigate to the RecipeActivity
            val intent = Intent(this, ListOfIngredientsActivity::class.java)
            intent.putExtra("ingredientList", ingredientList)
            startActivity(intent)
            // Clear the fields
            binding.searchBar.text.clear()
            binding.nutritionInfo.text = ""
        }

        binding.listOfAllRecipesBackButton.setOnClickListener() {
            finish()
        }

    }
}