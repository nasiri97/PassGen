package ir.ornix.passgen

import android.app.Application
import ir.ornix.passgen.core.common.isDebugBuild

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        isDebugBuild = BuildConfig.DEBUG
    }
}