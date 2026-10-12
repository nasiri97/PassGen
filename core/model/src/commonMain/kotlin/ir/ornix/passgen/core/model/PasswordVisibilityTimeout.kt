package ir.ornix.passgen.core.model

enum class PasswordVisibilityTimeout(val storageKey: String, val timeoutMillis: Long) {
    DISABLED("disabled", 0L),
    SECONDS_5("5s", 5_000L),
    SECONDS_10("10s", 10_000L),
    SECONDS_20("20s", 20_000L),
    SECONDS_30("30s", 30_000L),
    MINUTE_1("1m", 60_000L);

    companion object {
        val default = SECONDS_20
        fun fromKey(key: String?): PasswordVisibilityTimeout =
            entries.firstOrNull { it.storageKey.equals(key, ignoreCase = true) } ?: default
    }
}
