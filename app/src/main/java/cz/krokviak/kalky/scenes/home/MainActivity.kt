package cz.krokviak.kalky.scenes.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import android.os.Handler
import android.os.Looper
import cz.krokviak.kalky.core.app.AppContent
import cz.krokviak.kalky.core.camera.CameraActivity
import cz.krokviak.kalky.scenes.auth.AuthViewModel
import cz.krokviak.kalky.core.common.LocalPlatformActions
import cz.krokviak.kalky.core.common.PlatformActions
import cz.krokviak.kalky.config.RemoteConfigManager
import cz.krokviak.kalky.core.i18n.CzechStrings
import cz.krokviak.kalky.core.i18n.EnglishStrings
import cz.krokviak.kalky.core.common.AppLanguage
import cz.krokviak.kalky.core.common.AppPreferences
import cz.krokviak.kalky.core.notifications.MealReminderScheduler
import cz.krokviak.kalky.core.theme.KalkyTheme
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import org.koin.android.ext.android.inject
import java.io.File

class MainActivity : ComponentActivity() {
    // Only what MainActivity itself needs for camera-result handling and the
    // sign-in callback. Scenes resolve their own ViewModels via koinInject.
    private val mainViewModel: MainViewModel by inject()
    private val authViewModel: AuthViewModel by inject()
    private val appPreferences: AppPreferences by inject()

    private val cameraResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleCameraResult(result)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val platformActions = createAndroidPlatformActions()
            KalkyTheme {
                CompositionLocalProvider(LocalPlatformActions provides platformActions) {
                    AppContent()
                }
            }
        }

        Handler(Looper.getMainLooper()).post {
            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
            RemoteConfigManager.refresh()
            if (appPreferences.notificationsEnabled.value) {
                MealReminderScheduler.schedule(applicationContext)
            }
        }
    }

    private fun createAndroidPlatformActions(): PlatformActions {
        return object : PlatformActions {
            override fun launchCamera() {
                cameraResultLauncher.launch(
                    Intent(this@MainActivity, CameraActivity::class.java)
                )
            }

            override fun launchBarcodeScanner() {
                cameraResultLauncher.launch(
                    Intent(this@MainActivity, CameraActivity::class.java)
                )
            }

            override fun requestNotificationPermission() {
                // Handled by SettingsScene via rememberLauncherForActivityResult on Android
            }

            override fun shareImage(imagePath: String) {
                val file = File(imagePath)
                if (file.exists()) {
                    val uri = androidx.core.content.FileProvider.getUriForFile(
                        this@MainActivity,
                        "${packageName}.fileprovider",
                        file
                    )
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_STREAM, uri)
                        type = "image/jpeg"
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    startActivity(Intent.createChooser(sendIntent, null))
                }
            }

            override fun signInWithGoogle() {
                val webClientId = getString(cz.krokviak.kalky.R.string.default_web_client_id)
                authViewModel.signInWithGoogle(this@MainActivity, webClientId)
            }

            override fun signInWithApple() {
                // Not supported on Android
            }

            override fun isNotificationPermissionGranted(): Boolean {
                return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                        android.content.pm.PackageManager.PERMISSION_GRANTED
                } else {
                    true
                }
            }
        }
    }

    private fun handleCameraResult(result: ActivityResult) {
        if (result.resultCode != RESULT_OK) return
        val data = result.data ?: return
        when (data.getStringExtra(CameraActivity.EXTRA_RESULT_TYPE)) {
            CameraActivity.RESULT_TYPE_PHOTO -> handlePhotoResult(data)
            CameraActivity.RESULT_TYPE_BARCODE -> handleBarcodeResult(data)
        }
    }

    private fun handlePhotoResult(data: Intent) {
        val imageUrl = data.getStringExtra(CameraActivity.EXTRA_IMAGE_URL)
        val imageBytes = imageUrl?.let { File(it).readBytes() }
        if (imageBytes != null) {
            mainViewModel.addFoodItemFromBytes(imageBytes = imageBytes)
        }
    }

    private fun handleBarcodeResult(data: Intent) {
        val language = appPreferences.language.value
        mainViewModel.addFoodItemFromBarcode(
            name = data.getStringExtra(CameraActivity.EXTRA_NAME)
                ?: (if (language == AppLanguage.EN) EnglishStrings else CzechStrings).common.unknownProduct,
            calories = data.getIntExtra(CameraActivity.EXTRA_CALORIES, 0),
            protein = data.getIntExtra(CameraActivity.EXTRA_PROTEIN, 0),
            fat = data.getIntExtra(CameraActivity.EXTRA_FAT, 0),
            carbs = data.getIntExtra(CameraActivity.EXTRA_CARBS, 0)
        )
    }
}
