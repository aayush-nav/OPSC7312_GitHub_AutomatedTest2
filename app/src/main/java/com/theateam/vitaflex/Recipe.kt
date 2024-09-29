package com.theateam.vitaflex

import java.io.Serializable

data class Recipe(
    val name: String,
    val totalCalories: Double?,
    val ingredients: List<Ingredient>
) : Serializable

