package com.theateam.vitaflex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IngredientsAdapter(private val ingredients: List<Ingredient>) : RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder>() {

    class IngredientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ingredientName: TextView = view.findViewById(R.id.ingredient_name)
        val ingredientCalories: TextView = view.findViewById(R.id.ingredient_calories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ingredient_item, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.ingredientName.text = "${position+1}. " + ingredient.name
        holder.ingredientCalories.text = "${ingredient.calories} kcal"
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }
}
