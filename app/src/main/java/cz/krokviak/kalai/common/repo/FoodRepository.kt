package cz.krokviak.kalai.common.repo

import android.content.Context
import android.os.Environment
import cz.krokviak.kalai.analytics.CaloriesBar
import cz.krokviak.kalai.analytics.data.DailyMacroTotals
import cz.krokviak.kalai.common.entities.FoodItemEntity
import cz.krokviak.kalai.common.DatabaseProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import java.util.UUID

class FoodRepository(
    private val context: Context
) {
    private val dao = DatabaseProvider.instance.foodItemDao()

    /**
     * Save the given [imageBytes] to a unique file on external storage,
     * returning the absolute path to the file.
     */
    suspend fun storeImageFile(imageBytes: ByteArray): String = withContext(Dispatchers.IO) {
        val photosDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: throw IllegalStateException("Cannot access external files directory")

        val file = File(photosDir, "photo_${UUID.randomUUID()}.jpg")
        file.writeBytes(imageBytes)
        file.absolutePath
    }

    suspend fun getImageBytes(imagePath: String): ByteArray = withContext(Dispatchers.IO) {
        val file = File(imagePath)
        file.readBytes()
    }

    /**
     * Insert the [FoodItemEntity] into the DB. Returns the new row ID.
     */
    suspend fun insertFoodItem(item: FoodItemEntity): Long = withContext(Dispatchers.IO) {
        dao.insertFoodItem(item)
    }

    /**
     * Update the given [FoodItemEntity] in the DB (row must already exist).
     */
    suspend fun updateFoodItem(item: FoodItemEntity) = withContext(Dispatchers.IO) {
        dao.updateFoodItem(item)
    }

    /**
     * Example of retrieving items for a given date from the DB.
     */
    suspend fun getFoodItemsForDate(dateStr: String): List<FoodItemEntity> = withContext(Dispatchers.IO) {
        dao.getFoodItemsForDate(dateStr)
    }

    fun getFoodItem(foodId: Long): FoodItemEntity? {
        return dao.getFoodItem(foodId)
    }

    /**
     * Map from DayOfWeek to Czech short labels.
     */
    private val czechDayLabels = mapOf(
        DayOfWeek.MONDAY    to "Po",
        DayOfWeek.TUESDAY   to "Út",
        DayOfWeek.WEDNESDAY to "St",
        DayOfWeek.THURSDAY  to "Čt",
        DayOfWeek.FRIDAY    to "Pá",
        DayOfWeek.SATURDAY  to "So",
        DayOfWeek.SUNDAY    to "Ne"
    )

    fun getLast7DaysBars(): List<CaloriesBar> {
        // "now" is today's date
        val now = LocalDate.now()
        // We want the last 7 days BEFORE today, so end at "yesterday"
        val end = now
        // Start is 6 days before 'end', giving us a 7-day total window
        val start = end.minusDays(7)

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val startStr = start.format(formatter)
        val endStr   = end.format(formatter)

        // Query the DB for daily totals from start (7 days ago) to end (yesterday)
        val dailyTotals: List<DailyMacroTotals> =
            dao.getDailyMacroTotalsInRange(startStr, endStr)

        // Associate the totals by their exact date (assuming DailyMacroTotals.day is a LocalDate or a date string)
        val totalsByDate = dailyTotals.associateBy { it.day }

        // Build bars for each day in [start..end]
        val bars = mutableListOf<CaloriesBar>()
        for (i in 0..6) {
            val date = start.plusDays(i.toLong())

            // If you still want Czech labels indexed by day of week, you can do:
            // val dayOfWeek = date.dayOfWeek
            // val label = czechDayLabels[dayOfWeek] ?: "??"

            // Or you could label them by date or short weekday name:
            val label = czechDayLabels[date.dayOfWeek] ?: "??"

            val dayTotals = totalsByDate[date]  // match by the exact date
            val protein = dayTotals?.totalProtein ?: 0
            val carbs   = dayTotals?.totalCarbs   ?: 0
            val fat     = dayTotals?.totalFat     ?: 0

            bars.add(
                CaloriesBar(
                    label   = label,
                    protein = protein,
                    carbs   = carbs,
                    fat     = fat
                )
            )
        }

        return bars
    }

}
