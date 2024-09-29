package com.theateam.vitaflex

import MealExpandableListAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MealsActivity : AppCompatActivity() {

    private lateinit var expandableListView: ExpandableListView
    private lateinit var dayButton: Button
    private lateinit var weekButton: Button
    private lateinit var addMealButton: ImageButton
    private lateinit var backButton: ImageButton

    private lateinit var mealAdapter: MealExpandableListAdapter
    private lateinit var mealCategories: List<String>  // Changed to List since it doesn't need to be mutable
    private lateinit var mealItems: MutableMap<String, List<Meal>>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_meals)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        expandableListView = findViewById(R.id.MealsExpendableListView)
        dayButton = findViewById(R.id.MealsDaybtn)
        weekButton = findViewById(R.id.MealsWeekbtn)
        addMealButton = findViewById(R.id.MealsAddBtn)
        backButton = findViewById(R.id.MealsBackBtn)

        backButton.setOnClickListener {
            finish()
        }

        addMealButton.setOnClickListener {
            val intent = Intent(this, DailyMealsActivity::class.java)
            startActivity(intent)
        }

        dayButton.setOnClickListener { fetchMealsForDay() }
        weekButton.setOnClickListener { fetchMealsForWeek() }

        mealCategories = mutableListOf("Breakfast", "Lunch", "Dinner", "Snack")
        mealItems = mutableMapOf()

        // Initialize adapter with both mealCategories and mealItems
        mealAdapter = MealExpandableListAdapter(this, mealCategories, mealItems)
        expandableListView.setAdapter(mealAdapter)


    }

    private fun fetchMealsForDay() {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        ApiClient.apiService.getMeals().enqueue(object : Callback<List<Meal>> {
            override fun onResponse(call: Call<List<Meal>>, response: Response<List<Meal>>) {
                if (response.isSuccessful) {
                    val meals = response.body()
                    meals?.let {
                        val todayMeals = it.filter { meal -> meal.date == currentDate }
                        displayMealsByCategory(todayMeals)
                    }
                } else {
                    Toast.makeText(this@MealsActivity, "Failed to fetch meals for today", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Meal>>, t: Throwable) {
                Toast.makeText(this@MealsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchMealsForWeek() {
        val calendar = Calendar.getInstance()
        val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)

        ApiClient.apiService.getMeals().enqueue(object : Callback<List<Meal>> {
            override fun onResponse(call: Call<List<Meal>>, response: Response<List<Meal>>) {
                if (response.isSuccessful) {
                    val meals = response.body()
                    meals?.let {
                        val weekMeals = it.filter { meal ->
                            val mealDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(meal.date)
                            calendar.time = mealDate
                            calendar.get(Calendar.WEEK_OF_YEAR) == currentWeek
                        }
                        displayMealsByCategory(weekMeals)
                    }
                } else {
                    Toast.makeText(this@MealsActivity, "Failed to fetch meals for this week", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Meal>>, t: Throwable) {
                Toast.makeText(this@MealsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

//    private fun displayMealsByCategory(meals: List<Meal>) {
//        val breakfastMeals = meals.filter { it.mealCategory == "Breakfast" }
//        val lunchMeals = meals.filter { it.mealCategory == "Lunch" }
//        val dinnerMeals = meals.filter { it.mealCategory == "Dinner" }
//        val snackMeals = meals.filter { it.mealCategory == "Snack" }
//
//        // Update meal items map with filtered meals
//        mealItems["Breakfast"] = breakfastMeals
//        mealItems["Lunch"] = lunchMeals
//        mealItems["Dinner"] = dinnerMeals
//        mealItems["Snack"] = snackMeals
//
//        // Notify adapter about data changes
//        mealAdapter.notifyDataSetChanged()
//    }


    private fun displayMealsByCategory(meals: List<Meal>) {
        val groupedMeals = meals.groupBy { it.mealCategory } // Group meals by their category

        // Update meal items map with filtered meals
        mealItems.clear() // Clear existing items
        mealItems.putAll(groupedMeals) // Assign to mealItems

        // Notify adapter about data changes
        mealAdapter.notifyDataSetChanged()
    }


}