package ir.ornix.passgen

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ir.ornix.passgen.composeapp.di.initKoin
import ir.ornix.passgen.core.common.AppContextProvider
import ir.ornix.passgen.core.common.isAndroidDebugBuild
import ir.ornix.passgen.core.logging.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal var isDbInitializationCompleted by mutableStateOf(false)
internal var isDbInitializationFailed by mutableStateOf(false)

class MyApplication : Application() {
    private val scope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        isAndroidDebugBuild = BuildConfig.DEBUG
        AppContextProvider.applicationContext = this.applicationContext

        scope.launch {
            try {
                initKoin()
                isDbInitializationCompleted = true
            } catch (e: Exception) {
                Logger.e("Koin initialization failed.", e)
                isDbInitializationFailed = true
            }
        }
    }
}
