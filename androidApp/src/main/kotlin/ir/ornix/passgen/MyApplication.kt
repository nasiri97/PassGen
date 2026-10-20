package ir.ornix.passgen

import android.app.Application
import ir.ornix.passgen.core.common.isAndroidDebugBuild
import ir.ornix.passgen.core.database.initDatabaseContext

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        isAndroidDebugBuild = BuildConfig.DEBUG
        initDatabaseContext(this)
    }
}
