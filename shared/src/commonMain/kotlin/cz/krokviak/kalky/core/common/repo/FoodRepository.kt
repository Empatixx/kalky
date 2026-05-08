package cz.krokviak.kalky.core.common.repo

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import cz.krokviak.kalky.scenes.analytics.data.DailyMacroTotals
import cz.krokviak.kalky.core.common.entities.FoodItemEntity
import cz.krokviak.kalky.core.db.KalkyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

data class MacroTotals(
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
)

open class FoodRepository(
    private val database: KalkyDatabase
) {
    private val queries get() = database.foodItemQueries

    open suspend fun insertFoodItem(item: FoodItemEntity): Long = withContext(Dispatchers.IO) {
        database.transactionWithResult {
            queries.insertFoodItem(
                name = item.name,
                calories = item.calories,
                protein = item.protein,
                fat = item.fat,
                carbs = item.carbs,
                portion = item.portion,
                healthScore = item.healthScore,
                createdAt = item.createdAt.toString(),
                updatedAt = item.updatedAt.toString(),
                localImagePath = item.localImagePath,
                loading = item.loading,
                isCustom = item.isCustom
            )
            queries.lastInsertRowId().executeAsOne()
        }
    }

    open suspend fun updateFoodItem(item: FoodItemEntity) = withContext(Dispatchers.IO) {
        queries.updateFoodItem(
            name = item.name,
            calories = item.calories,
            protein = item.protein,
            fat = item.fat,
            carbs = item.carbs,
            portion = item.portion,
            healthScore = item.healthScore,
            updatedAt = item.updatedAt.toString(),
            localImagePath = item.localImagePath,
            loading = item.loading,
            id = item.id
        )
    }

    open suspend fun getFoodItemsForDate(dateStr: String): List<FoodItemEntity> = withContext(Dispatchers.IO) {
        queries.getFoodItemsForDate(dateStr).executeAsList().map { it.toEntity() }
    }

    open fun observeFoodItemsForDate(dateStr: String): Flow<List<FoodItemEntity>> =
        queries.getFoodItemsForDate(dateStr)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toEntity() } }

    open fun observeMacroTotalsForDate(dateStr: String): Flow<MacroTotals> =
        queries.getMacroTotalsForDate(dateStr)
            .asFlow()
            .mapToOne(Dispatchers.IO)
            .map { row ->
                MacroTotals(
                    calories = row.totalCalories.toInt(),
                    protein = row.totalProtein.toInt(),
                    carbs = row.totalCarbs.toInt(),
                    fat = row.totalFat.toInt(),
                )
            }

    open suspend fun getFoodItem(foodId: Long): FoodItemEntity? = withContext(Dispatchers.IO) {
        queries.getFoodItem(foodId).executeAsOneOrNull()?.toEntity()
    }

    open suspend fun getTotalCaloriesForDate(dateStr: String): Int = withContext(Dispatchers.IO) {
        queries.getTotalCaloriesForDate(dateStr).executeAsOneOrNull()?.SUM ?: 0
    }

    open suspend fun getTotalFatsForDate(dateStr: String): Int = withContext(Dispatchers.IO) {
        queries.getTotalFatsForDate(dateStr).executeAsOneOrNull()?.SUM ?: 0
    }

    open suspend fun getTotalCarbsForDate(dateStr: String): Int = withContext(Dispatchers.IO) {
        queries.getTotalCarbsForDate(dateStr).executeAsOneOrNull()?.SUM ?: 0
    }

    open suspend fun getTotalProteinForDate(dateStr: String): Int = withContext(Dispatchers.IO) {
        queries.getTotalProteinForDate(dateStr).executeAsOneOrNull()?.SUM ?: 0
    }

    open suspend fun getMacroTotalsForDate(dateStr: String): MacroTotals = withContext(Dispatchers.IO) {
        val row = queries.getMacroTotalsForDate(dateStr).executeAsOne()
        MacroTotals(
            calories = row.totalCalories.toInt(),
            protein = row.totalProtein.toInt(),
            carbs = row.totalCarbs.toInt(),
            fat = row.totalFat.toInt(),
        )
    }

    open suspend fun deleteFoodItem(id: Long) = withContext(Dispatchers.IO) {
        queries.deleteFoodItem(id)
    }

    open suspend fun getDistinctFoodsByName(): List<FoodItemEntity> = withContext(Dispatchers.IO) {
        queries.getDistinctFoodsByName().executeAsList().map { it.toEntity() }
    }

    open suspend fun searchDistinctFoodsByName(query: String): List<FoodItemEntity> = withContext(Dispatchers.IO) {
        queries.searchDistinctFoodsByName(query).executeAsList().map { it.toEntity() }
    }

    open suspend fun getCustomFoods(): List<FoodItemEntity> = withContext(Dispatchers.IO) {
        queries.getCustomFoods().executeAsList().map { it.toEntity() }
    }

    open suspend fun searchCustomFoods(query: String): List<FoodItemEntity> = withContext(Dispatchers.IO) {
        queries.searchCustomFoods(query).executeAsList().map { it.toEntity() }
    }

    open suspend fun getDistinctFoodDates(): List<String> = withContext(Dispatchers.IO) {
        queries.getDistinctFoodDates().executeAsList()
    }

    open suspend fun getRecentDistinctFoodDates(limit: Long): List<String> = withContext(Dispatchers.IO) {
        queries.getRecentDistinctFoodDates(limit).executeAsList()
    }

    open suspend fun getDailyMacroTotalsInRange(
        startDate: String,
        endDate: String
    ): List<DailyMacroTotals> = withContext(Dispatchers.IO) {
        queries.getDailyMacroTotalsInRange(startDate, endDate).executeAsList().map { row ->
            DailyMacroTotals(
                day = LocalDate.parse(row.day!!),
                totalProtein = row.totalProtein,
                totalCarbs = row.totalCarbs,
                totalFat = row.totalFat
            )
        }
    }
}

private fun cz.krokviak.kalky.Food_items.toEntity() = FoodItemEntity(
    id = id,
    name = name,
    calories = calories,
    protein = protein,
    fat = fat,
    carbs = carbs,
    portion = portion,
    healthScore = healthScore,
    createdAt = Instant.parse(createdAt),
    updatedAt = Instant.parse(updatedAt),
    localImagePath = localImagePath,
    loading = loading,
    isCustom = isCustom
)
