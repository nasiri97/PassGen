package ir.ornix.passgen

import android.app.Application
import ir.ornix.passgen.composeapp.di.initKoin
import ir.ornix.passgen.core.common.AppContextProvider
import ir.ornix.passgen.core.common.isAndroidDebugBuild
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

internal var isDbInitializationCompleted = false
internal var isDbInitializationFailed = false

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
            } catch (_: Exception) {
                isDbInitializationFailed = true
            }
        }
    }
}
