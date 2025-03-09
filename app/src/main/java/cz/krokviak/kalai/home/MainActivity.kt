package cz.krokviak.kalai.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import cz.krokviak.kalai.camera.CameraActivity
import cz.krokviak.kalai.camera.entities.FoodItemEntity
import cz.krokviak.kalai.screen.MainScreen
import org.threeten.bp.LocalDate

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private var currentDate: LocalDate = LocalDate.now()

    // 1) Create a launcher for CameraActivity
    private val cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // 2) Check if the result was OK
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                data?.let { intent ->
                    val foodEntity = intent.getSerializableExtra("foodEntity") as? FoodItemEntity
                    foodEntity?.let {
                        // 3) Add the food item to the view model
                        mainViewModel.addFoodItem(it)
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel.loadFoodItemsForDate(currentDate)

        setContent {
            MaterialTheme {
                MainScreen(
                    onCaptureClick = {
                        // 4) Instead of startActivityForResult, use the launcher
                        val intent = Intent(this, CameraActivity::class.java)
                        cameraResultLauncher.launch(intent)
                    },
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}
