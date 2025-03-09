package cz.krokviak.kalai.camera.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query
import cz.krokviak.kalai.camera.entities.FoodItemEntity

@Dao
interface FoodItemDao {

    @Query("SELECT * FROM food_items ORDER BY createdAt DESC")
    fun getAllFoodItems(): List<FoodItemEntity>

    // Select all food items for a specific date (comparing only the date portion of createdAt)
    @Query("SELECT * FROM food_items WHERE date(createdAt) = :date")
    fun getFoodItemsForDate(date: String): List<FoodItemEntity>

    // Get total calories for a specific date
    @Query("SELECT SUM(calories) FROM food_items WHERE date(createdAt) = :date")
    fun getTotalCaloriesForDate(date: String): Int?

    // Get total fats for a specific date
    @Query("SELECT SUM(fat) FROM food_items WHERE date(createdAt) = :date")
    fun getTotalFatsForDate(date: String): Int?

    // Get total carbs for a specific date
    @Query("SELECT SUM(carbs) FROM food_items WHERE date(createdAt) = :date")
    fun getTotalCarbsForDate(date: String): Int?

    // Get total protein for a specific date
    @Query("SELECT SUM(protein) FROM food_items WHERE date(createdAt) = :date")
    fun getTotalProteinForDate(date: String): Int?

    @Insert
    fun insertFoodItem(foodItem: FoodItemEntity): Long

    @Delete
    fun deleteFoodItem(foodItem: FoodItemEntity)
}
