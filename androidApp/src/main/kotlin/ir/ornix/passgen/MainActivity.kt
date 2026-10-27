package ir.ornix.passgen

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.view.autofill.AutofillManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LifecycleResumeEffect
import ir.ornix.passgen.autofill.requestEnableAutofill
import ir.ornix.passgen.composeapp.App
import ir.ornix.passgen.composeapp.DeviceSupportStatus
import ir.ornix.passgen.core.common.AppContextProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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

        setContent {
            var isDeviceRooted by remember { mutableStateOf<Boolean?>(null) }

            LaunchedEffect(Unit) {
                isDeviceRooted = withContext(Dispatchers.Default) {
                    RootDetector.isDeviceRooted(applicationContext)
                }
            }

            val supportStatus = when {
                isDeviceRooted == true -> DeviceSupportStatus.ROOTED
                isDbInitializationFailed -> DeviceSupportStatus.INITIALIZATION_FAILED
                isDeviceRooted == false && isDbInitializationCompleted -> DeviceSupportStatus.SUPPORTED
                else -> null
            }

            App(
                supportStatus = supportStatus,
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

@Composable
fun AutofillSetupButton() {
    val activity = LocalActivity.current ?: return
    val manager = remember(activity) { activity.getSystemService(AutofillManager::class.java) }
    var enabled by remember { mutableStateOf(manager?.hasEnabledAutofillServices() == true) }

    LifecycleResumeEffect(Unit) {
        enabled = manager?.hasEnabledAutofillServices() == true
        onPauseOrDispose { }
    }

    if (!enabled) {
        Button(onClick = { activity.requestEnableAutofill() }) {
            Text("Enable autofill")
        }
    } else {
        Text("Autofill is enabled")
    }
}