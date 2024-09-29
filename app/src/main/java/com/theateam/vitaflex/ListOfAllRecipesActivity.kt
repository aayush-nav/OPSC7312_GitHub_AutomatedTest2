package com.theateam.vitaflex

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.theateam.vitaflex.databinding.ActivityListOfAllRecipesBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListOfAllRecipesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListOfAllRecipesBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private var recipeList = ArrayList<Recipe>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityListOfAllRecipesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setContentView(R.layout.activity_list_of_all_recipes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.listOfAllRecipesCreateRecipeBtn.setOnClickListener {
            val intent = Intent(this@ListOfAllRecipesActivity, AddIngredientActivity::class.java)
            startActivity(intent)
        }
        recyclerView = findViewById(R.id.listOfAllRecipes_recipe_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load saved recipes from API
        loadRecipes()

        binding.swipeRefresh.setOnRefreshListener {
            loadRecipes()
            binding.swipeRefresh.isRefreshing = false
        }

        recipeAdapter = RecipeAdapter(recipeList) { recipe ->
            // Handle recipe click
            val intent = Intent(this, RecipeDetailActivity::class.java)
            intent.putExtra("recipe", recipe)
            startActivity(intent)
        }
        recyclerView.adapter = recipeAdapter

    }


    private fun loadRecipes() {
        // Make the API call to fetch recipes
        ApiClient.apiService.getRecipes().enqueue(object : Callback<List<Recipe>> {
            override fun onResponse(call: Call<List<Recipe>>, response: Response<List<Recipe>>) {
                if (response.isSuccessful && response.body() != null) {
                    // Update the recipe list and notify the adapter
                    recipeList.clear()
                    recipeList.addAll(response.body()!!)
                    recipeAdapter.notifyDataSetChanged()
                    // Log the data for debugging
                    Log.d("API_SUCCESS", "Recipes fetched successfully: ${response.body()}")
                } else {
                    // Handle the error
                    Log.e("API_ERROR", "Failed to load recipes. Error code: ${response.code()}, message: ${response.message()}")
                    Toast.makeText(this@ListOfAllRecipesActivity, "Failed to load recipes", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Recipe>>, t: Throwable) {
                // Log the error
                Log.e("API_FAILURE", "Error: ${t.message}", t)
                Toast.makeText(this@ListOfAllRecipesActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }


}