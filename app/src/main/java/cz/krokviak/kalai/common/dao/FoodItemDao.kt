package cz.krokviak.kalai.common.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import cz.krokviak.kalai.analytics.data.DailyMacroTotals
import cz.krokviak.kalai.common.entities.FoodItemEntity

@Dao
interface FoodItemDao {

    @Query("SELECT * FROM food_items ORDER BY createdAt DESC")
    fun getAllFoodItems(): List<FoodItemEntity>

    // Select all food items for a specific date (comparing only the date portion of createdAt)
    @Query("SELECT * FROM food_items WHERE date(createdAt) = :date ORDER BY createdAt DESC")
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

    @Update
    fun updateFoodItem(foodItem: FoodItemEntity)

    @Delete
    fun deleteFoodItem(foodItem: FoodItemEntity)

    @Query("SELECT * FROM food_items WHERE id = :foodId")
    fun getFoodItem(foodId: Long): FoodItemEntity?

    // Group by the date portion of createdAt
    @Query("""
        SELECT 
            date(createdAt) as day,
            SUM(protein) as totalProtein,
            SUM(carbs)   as totalCarbs,
            SUM(fat)     as totalFat
        FROM food_items
        WHERE date(createdAt) BETWEEN :startDate AND :endDate
        GROUP BY date(createdAt)
        ORDER BY day ASC
    """)
    fun getDailyMacroTotalsInRange(
        startDate: String,
        endDate: String
    ): List<DailyMacroTotals>

}
