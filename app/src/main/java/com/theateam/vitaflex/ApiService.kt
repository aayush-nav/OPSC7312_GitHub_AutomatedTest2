package com.theateam.vitaflex

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    // POST to add a recipe
    @POST("/addrecipe")
    fun addRecipe(@Body recipe: Recipe): Call<ResponseBody>

    // GET to retrieve all recipes
    @GET("/getrecipes")
    fun getRecipes(): Call<List<Recipe>>

    @POST("/addmeal")
    fun addMeal(@Body meal: Meal): Call<ResponseBody>

    @GET("/getmeals")
    fun getMeals(): Call<List<Meal>>

}