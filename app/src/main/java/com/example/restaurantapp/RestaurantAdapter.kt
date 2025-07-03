package com.example.restaurantapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp.models.Restaurant

class RestaurantAdapter(
    private var restaurants: List<Restaurant> = emptyList(),
    private val onItemClick: (Restaurant) -> Unit = {}
) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRestaurantName: TextView = itemView.findViewById(R.id.tvRestaurantName)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        val tvCuisineType: TextView = itemView.findViewById(R.id.tvCuisineType)
        val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        val tvPhone: TextView = itemView.findViewById(R.id.tvPhone)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restaurant, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = restaurants[position]

        holder.tvRestaurantName.text = restaurant.name
        holder.tvRating.text = "★ ${restaurant.rating}"
        holder.tvCuisineType.text = restaurant.cuisineType
        holder.tvAddress.text = restaurant.address
        holder.tvPhone.text = restaurant.phone
        holder.tvDescription.text = restaurant.description

        holder.itemView.setOnClickListener {
            onItemClick(restaurant)
        }
    }

    override fun getItemCount(): Int = restaurants.size

    fun updateRestaurants(newRestaurants: List<Restaurant>) {
        restaurants = newRestaurants
        notifyDataSetChanged()
    }
}