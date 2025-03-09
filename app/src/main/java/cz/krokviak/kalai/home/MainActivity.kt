package cz.krokviak.kalai.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import cz.krokviak.kalai.camera.CameraActivity
import cz.krokviak.kalai.screen.MainScreen

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainScreen(
                    onCaptureClick = {
                        val intent = Intent(this, CameraActivity::class.java)
                        startActivity(intent)
                    },
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}
