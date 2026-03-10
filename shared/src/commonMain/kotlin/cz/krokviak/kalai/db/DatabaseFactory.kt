package cz.krokviak.kalai.db

import app.cash.sqldelight.ColumnAdapter
import cz.krokviak.kalai.Food_items
import cz.krokviak.kalai.Nutrient_settings
import cz.krokviak.kalai.Personal_info
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus

private val intColumnAdapter = object : ColumnAdapter<Int, Long> {
    override fun decode(databaseValue: Long) = databaseValue.toInt()
    override fun encode(value: Int) = value.toLong()
}

private val booleanColumnAdapter = object : ColumnAdapter<Boolean, Long> {
    override fun decode(databaseValue: Long) = databaseValue != 0L
    override fun encode(value: Boolean) = if (value) 1L else 0L
}

private val floatColumnAdapter = object : ColumnAdapter<Float, Double> {
    override fun decode(databaseValue: Double) = databaseValue.toFloat()
    override fun encode(value: Float) = value.toDouble()
}

fun createDatabase(driverFactory: DriverFactory): KalaiDatabase {
    val driver = driverFactory.createDriver()
    val db = KalaiDatabase(
        driver = driver,
        food_itemsAdapter = Food_items.Adapter(
            caloriesAdapter = intColumnAdapter,
            proteinAdapter = intColumnAdapter,
            fatAdapter = intColumnAdapter,
            carbsAdapter = intColumnAdapter,
            portionAdapter = intColumnAdapter,
            healthScoreAdapter = intColumnAdapter
        ),
        personal_infoAdapter = Personal_info.Adapter(
            ageAdapter = intColumnAdapter,
            heightCmAdapter = floatColumnAdapter,
            weightKgAdapter = floatColumnAdapter,
            activityLevelAdapter = intColumnAdapter
        ),
        nutrient_settingsAdapter = Nutrient_settings.Adapter(
            targetCaloriesAdapter = intColumnAdapter,
            targetProteinAdapter = intColumnAdapter,
            targetFatAdapter = intColumnAdapter,
            targetCarbsAdapter = intColumnAdapter
        )
    )
    seedMockData(db)
    return db
}

private fun seedMockData(db: KalaiDatabase) {
    val existing = db.personalInfoQueries.getLatestPersonalInfo().executeAsOneOrNull()
    if (existing != null) return

    val now = Clock.System.now()
    val tz = TimeZone.currentSystemDefault()

    // Mock weights for last 7 days
    val weights = listOf(82.5f, 82.1f, 81.8f, 82.0f, 81.5f, 81.2f, 80.9f)
    weights.forEachIndexed { i, weight ->
        val instant = now.minus((6 - i), DateTimeUnit.DAY, tz)
        val timestamp = instant.toString()
        db.personalInfoQueries.insertPersonalInfo(
            gender = "Muž",
            age = 25,
            heightCm = 180f,
            weightKg = weight,
            activityLevel = 2,
            createdAt = timestamp,
            updatedAt = timestamp
        )
    }

    // Mock nutrient targets
    db.nutrientSettingQueries.insertNutrientSettings(
        targetProtein = 150,
        targetCarbs = 250,
        targetFat = 70,
        targetCalories = (150 * 4) + (250 * 4) + (70 * 9),
        createdAt = now.toString(),
        updatedAt = now.toString()
    )

    // Mock food items for last 5 days
    data class MockFood(val name: String, val calories: Int, val protein: Int, val fat: Int, val carbs: Int, val healthScore: Int)

    val dailyMeals = listOf(
        listOf(
            MockFood("Ovesná kaše s ovocem", 350, 12, 8, 55, 9),
            MockFood("Kuřecí salát", 420, 35, 18, 25, 8),
            MockFood("Losos s rýží", 580, 40, 22, 50, 9),
            MockFood("Řecký jogurt", 150, 15, 5, 12, 8),
        ),
        listOf(
            MockFood("Vejce na hniličku", 280, 18, 16, 8, 7),
            MockFood("Hovězí burger", 650, 38, 32, 40, 5),
            MockFood("Těstoviny s kuřecím", 520, 30, 14, 60, 7),
            MockFood("Proteinový koktejl", 200, 30, 3, 12, 7),
        ),
        listOf(
            MockFood("Palačinky", 400, 10, 12, 58, 5),
            MockFood("Tuňákový wrap", 380, 28, 12, 35, 8),
            MockFood("Kuřecí steak s bramborem", 550, 42, 15, 45, 8),
            MockFood("Cottage cheese", 120, 14, 4, 6, 8),
        ),
        listOf(
            MockFood("Müsli s mlékem", 320, 10, 6, 52, 7),
            MockFood("Krůtí sendvič", 450, 32, 16, 38, 7),
            MockFood("Vepřové s knedlíkem", 680, 35, 28, 55, 5),
            MockFood("Jablko", 80, 0, 0, 20, 10),
        ),
        listOf(
            MockFood("Avokádový toast", 310, 8, 18, 28, 8),
            MockFood("Poke bowl", 490, 30, 16, 52, 9),
        ),
    )

    dailyMeals.forEachIndexed { dayIndex, meals ->
        val daysAgo = dailyMeals.size - 1 - dayIndex
        meals.forEach { food ->
            val instant = now.minus(daysAgo, DateTimeUnit.DAY, tz)
            val timestamp = instant.toString()
            db.foodItemQueries.insertFoodItem(
                name = food.name,
                calories = food.calories,
                protein = food.protein,
                fat = food.fat,
                carbs = food.carbs,
                portion = 1,
                healthScore = food.healthScore,
                createdAt = timestamp,
                updatedAt = timestamp,
                localImagePath = "",
                loading = false
            )
        }
    }
}
