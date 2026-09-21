package ir.ornix.passgen

import android.app.Application
import ir.ornix.passgen.core.common.isAndroidDebugBuild

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        isAndroidDebugBuild = BuildConfig.DEBUG
    }
}