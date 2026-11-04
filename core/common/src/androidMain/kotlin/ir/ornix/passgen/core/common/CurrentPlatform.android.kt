package ir.ornix.passgen.core.common

import android.os.Build

actual val platform: Platform
    get() = Platform.Android(
        apiLevel = Build.VERSION.SDK_INT,
        isDebugBuild = AppContextProvider.isDebugBuild
    )