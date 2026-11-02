package ir.ornix.passgen.core.model

data class InstalledAppInfo(
    val appName: String,
    val packageName: String,
    val icon: Any? = null
)
