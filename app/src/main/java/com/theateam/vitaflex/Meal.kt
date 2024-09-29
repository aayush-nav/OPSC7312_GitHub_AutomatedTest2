package com.theateam.vitaflex

import java.io.Serializable

data class Meal(
    val email: String,
    val date: String,
    val mealCategory: String,
    val mealItems: List<MealType> // Changed to List<MealType> to reflect multiple items
) : Serializable