package ir.ornix.passgen

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import ir.ornix.passgen.composeapp.App
import ir.ornix.passgen.composeapp.FailedInitializationReason
import ir.ornix.passgen.composeapp.InitializationState
import ir.ornix.passgen.core.common.AppContextProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        AppContextProvider.setActivity(this)

        /**
         * Prevent Screenshots & Screen Recording (FLAG_SECURE)
         */
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        /**
         * prevents other apps' overlay windows (like "draw over other apps" permission overlays) from being shown on top of your activity.
         * It's a security feature to block tapjacking/overlay attacks
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window.setHideOverlayWindows(true)
        }

        splashScreen.setKeepOnScreenCondition { false }

        setContent {
            var isDeviceRooted by rememberSaveable { mutableStateOf<Boolean?>(null) }

            LaunchedEffect(Unit) {
                isDeviceRooted = withContext(Dispatchers.Default) {
                    RootDetector.isDeviceRooted(applicationContext)
                }
            }

            val initializationState = when {
                isDeviceRooted == true -> InitializationState.Failed(FailedInitializationReason.ROOTED)
                isDbInitializationFailed -> InitializationState.Failed(FailedInitializationReason.INITIALIZATION_FAILED)
                isDeviceRooted == false && isDbInitializationCompleted -> InitializationState.Completed
                else -> InitializationState.Initializing
            }

            App(
                initializationState = initializationState,
                onExit = {
                    this.finishAffinity()
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        AppContextProvider.setActivity(this)
    }
}