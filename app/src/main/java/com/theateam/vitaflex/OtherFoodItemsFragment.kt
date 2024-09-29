package com.theateam.vitaflex

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.google.gson.Gson
import com.theateam.vitaflex.databinding.FragmentOtherFoodItemsBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale

class OtherFoodItemsFragment : Fragment() {

    private val API_BASE_URL = "https://api.calorieninjas.com/"
    private val API_KEY = "24Dq75ydbIBCI6KpY2+BeQ==SJTYu7ebGCugjYj5" // Add your CaloriesNinjas API key here

    private var _binding: FragmentOtherFoodItemsBinding? = null
    private val binding get() = _binding!!

    private var ingredientList: ArrayList<Ingredient> = ArrayList()

    private var selectedDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOtherFoodItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
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

        binding.searchButton.setOnClickListener {
            val query = binding.searchBar.text.toString()

            if (query.isNotEmpty()) {
                // Make the API call
                val call = api.searchFood(API_KEY, query)

                call?.enqueue(object : Callback<NutritionResponse?> {
                    override fun onFailure(call: Call<NutritionResponse?>, t: Throwable) {
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
//            val ingredientProtein = currentProtein
//            val ingredientCarbs = currentCarbs
//            val ingredientCholesterol = currentCholesterol
//            val ingredientFatTotal = currentFatTotal
//            val ingredientFiber = currentFiber
//            val ingredientSugar = currentSugar

//            val ingredient = Ingredient(ingredientName, ingredientCalories, ingredientProtein, ingredientCarbs,
//                ingredientCholesterol, ingredientFatTotal, ingredientFiber, ingredientSugar)
//            ingredientList.add(ingredient)

//            // Navigate to ListOfIngredientsActivity
//            val intent = Intent(requireContext(), ListOfIngredientsActivity::class.java)
//            intent.putExtra("ingredientList", ingredientList)
//            startActivity(intent)

            // Clear the fields
            binding.searchBar.text.clear()
            binding.nutritionInfo.text = ""

            val userEmail = "user@example.com"

            selectedDate = getSelectedDate() // Retrieve the saved date
            Log.d("NextActivity", "Selected date: $selectedDate")

            val formattedDate = convertDate(selectedDate!!)
            Log.d("NextActivity", "Formatted date: $formattedDate")

            val sharedPreferencesMeals = requireContext().getSharedPreferences("MyMealCategoryPreferences", MODE_PRIVATE)
            val mealCategory = sharedPreferencesMeals.getString("mealCategory", null)


            val mealItem = MealType(ingredientName, ingredientCalories!!)

            saveMeal(userEmail, formattedDate, mealCategory!!, mealItem)

        }







    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addMeal(meal: Meal){

        val gson = Gson()
        val mealJson = gson.toJson(meal)

        Log.d("JSON_REQUEST", mealJson) // Log the JSON request
        ApiClient.apiService.addMeal(meal).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
//                    Toast.makeText(this@OtherFoodItemsFragment, "Recipe added successfully", Toast.LENGTH_SHORT).show()
                    Log.d("API_SUCCESS", "Recipe added successfully: ${response.body()?.string()}")
                } else {
//                    Toast.makeText(this@OtherFoodItemsFragment, "Failed to add recipe", Toast.LENGTH_SHORT).show()
                    Log.e("API_ERROR", "Error: ${response.code()}, ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("API_FAILURE", "Failed to make request: ${t.message}")
//                Toast.makeText(this@OtherFoodItemsFragment, "Error: ${t.message}", Toast.LENGTH_LONG).show()
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
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        return sharedPreferences.getString("selectedDate", null)
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

}
