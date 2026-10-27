package ir.ornix.passgen.core.model

enum class AppTheme(val storageKey: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark");

    companion object {
        fun fromKey(key: String?): AppTheme =
            entries.firstOrNull { it.storageKey.equals(key, ignoreCase = true) } ?: SYSTEM
    }
}
