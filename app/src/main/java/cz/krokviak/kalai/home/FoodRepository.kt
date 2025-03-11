package cz.krokviak.kalai.home

import android.content.Context
import android.os.Environment
import cz.krokviak.kalai.camera.entities.FoodItemEntity
import cz.krokviak.kalai.common.DatabaseProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime
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

    /**
     * Additional DB operations like getTotalCaloriesForDate, etc., if needed.
     */
}
