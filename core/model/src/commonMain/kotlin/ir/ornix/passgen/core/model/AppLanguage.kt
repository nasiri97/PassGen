package ir.ornix.passgen.core.model

enum class AppLanguage(val code: String) {
    SYSTEM("system"),
    ENGLISH("en"),
    PERSIAN("fa");

    companion object {
        fun fromCode(code: String?): AppLanguage =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: SYSTEM
    }
}
