package cz.krokviak.kalky.core.db

import app.cash.sqldelight.ColumnAdapter
import cz.krokviak.kalky.Food_items
import cz.krokviak.kalky.Nutrient_settings
import cz.krokviak.kalky.Personal_info

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

fun createDatabase(driverFactory: DriverFactory): KalkyDatabase {
    val driver = driverFactory.createDriver()
    return KalkyDatabase(
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
}
