package cz.krokviak.kalky.core.common.repo

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.turbine.test
import cz.krokviak.kalky.Food_items
import cz.krokviak.kalky.Nutrient_settings
import cz.krokviak.kalky.Personal_info
import cz.krokviak.kalky.core.common.entities.FoodItemEntity
import cz.krokviak.kalky.core.db.KalkyDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FoodRepositoryTest {

    private lateinit var driver: JdbcSqliteDriver
    private lateinit var db: KalkyDatabase
    private lateinit var repo: FoodRepository

    private val today = Instant.parse("2026-05-08T10:00:00Z")
    private val yesterday = Instant.parse("2026-05-07T10:00:00Z")

    private fun item(
        name: String = "Test",
        calories: Int = 100,
        protein: Int = 10,
        fat: Int = 5,
        carbs: Int = 15,
        createdAt: Instant = today,
        isCustom: Boolean = false,
    ) = FoodItemEntity(
        name = name,
        calories = calories,
        protein = protein,
        fat = fat,
        carbs = carbs,
        healthScore = 7,
        createdAt = createdAt,
        updatedAt = createdAt,
        localImagePath = "",
        loading = false,
        isCustom = isCustom,
    )

    @BeforeTest
    fun setUp() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        KalkyDatabase.Schema.create(driver)
        val intAdapter = object : ColumnAdapter<Int, Long> {
            override fun decode(databaseValue: Long) = databaseValue.toInt()
            override fun encode(value: Int) = value.toLong()
        }
        val floatAdapter = object : ColumnAdapter<Float, Double> {
            override fun decode(databaseValue: Double) = databaseValue.toFloat()
            override fun encode(value: Float) = value.toDouble()
        }
        db = KalkyDatabase(
            driver = driver,
            food_itemsAdapter = Food_items.Adapter(
                caloriesAdapter = intAdapter,
                proteinAdapter = intAdapter,
                fatAdapter = intAdapter,
                carbsAdapter = intAdapter,
                portionAdapter = intAdapter,
                healthScoreAdapter = intAdapter,
            ),
            nutrient_settingsAdapter = Nutrient_settings.Adapter(
                targetCaloriesAdapter = intAdapter,
                targetProteinAdapter = intAdapter,
                targetFatAdapter = intAdapter,
                targetCarbsAdapter = intAdapter,
            ),
            personal_infoAdapter = Personal_info.Adapter(
                ageAdapter = intAdapter,
                heightCmAdapter = floatAdapter,
                weightKgAdapter = floatAdapter,
                activityLevelAdapter = intAdapter,
            ),
        )
        repo = FoodRepository(db)
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    @Test
    fun insertFoodItem_returnsId_andItemIsRetrievable() = runTest {
        val id = repo.insertFoodItem(item(name = "Banán", calories = 90))

        assertTrue(id > 0)
        val fetched = repo.getFoodItem(id)
        assertNotNull(fetched)
        assertEquals("Banán", fetched.name)
        assertEquals(90, fetched.calories)
    }

    @Test
    fun deleteFoodItem_removesRow() = runTest {
        val id = repo.insertFoodItem(item())

        repo.deleteFoodItem(id)

        assertNull(repo.getFoodItem(id))
    }

    @Test
    fun observeFoodItemsForDate_emitsUpdatedListAfterInsert() = runTest {
        val dateStr = "2026-05-08"

        repo.observeFoodItemsForDate(dateStr).test {
            assertTrue(awaitItem().isEmpty())

            repo.insertFoodItem(item(name = "Avokádo"))

            val next = awaitItem()
            assertEquals(1, next.size)
            assertEquals("Avokádo", next.first().name)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeMacroTotalsForDate_aggregatesAllItems() = runTest {
        val dateStr = "2026-05-08"
        repo.insertFoodItem(item(calories = 100, protein = 10, fat = 5, carbs = 15))
        repo.insertFoodItem(item(calories = 200, protein = 20, fat = 10, carbs = 30))

        val totals = repo.observeMacroTotalsForDate(dateStr).first()

        assertEquals(300, totals.calories)
        assertEquals(30, totals.protein)
        assertEquals(15, totals.fat)
        assertEquals(45, totals.carbs)
    }

    @Test
    fun getCustomFoods_returnsOnlyCustomItems() = runTest {
        repo.insertFoodItem(item(name = "Photo food", isCustom = false))
        repo.insertFoodItem(item(name = "Custom A", isCustom = true))
        repo.insertFoodItem(item(name = "Custom B", isCustom = true))

        val customs = repo.getCustomFoods()

        assertEquals(2, customs.size)
        assertTrue(customs.all { it.isCustom })
    }

    @Test
    fun getFoodItemsForDate_filtersByDate() = runTest {
        repo.insertFoodItem(item(name = "Today", createdAt = today))
        repo.insertFoodItem(item(name = "Yesterday", createdAt = yesterday))

        val todayItems = repo.getFoodItemsForDate("2026-05-08")
        val yesterdayItems = repo.getFoodItemsForDate("2026-05-07")

        assertEquals(1, todayItems.size)
        assertEquals("Today", todayItems.single().name)
        assertEquals(1, yesterdayItems.size)
        assertEquals("Yesterday", yesterdayItems.single().name)
    }

    @Test
    fun updateFoodItem_persistsNewValues() = runTest {
        val id = repo.insertFoodItem(item(name = "Original", calories = 100))
        val original = repo.getFoodItem(id)!!

        repo.updateFoodItem(original.copy(name = "Updated", calories = 200))

        val updated = repo.getFoodItem(id)!!
        assertEquals("Updated", updated.name)
        assertEquals(200, updated.calories)
    }
}
