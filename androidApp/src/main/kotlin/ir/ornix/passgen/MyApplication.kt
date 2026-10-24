package ir.ornix.passgen

import android.app.Application
import ir.ornix.passgen.composeapp.di.appModule
import ir.ornix.passgen.core.common.AppContextProvider
import ir.ornix.passgen.core.common.isAndroidDebugBuild
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        isAndroidDebugBuild = BuildConfig.DEBUG
        AppContextProvider.applicationContext = this.applicationContext

        if (GlobalContext.getOrNull() == null) {
            startKoin {
                modules(appModule)
            }
        }
    }
}
