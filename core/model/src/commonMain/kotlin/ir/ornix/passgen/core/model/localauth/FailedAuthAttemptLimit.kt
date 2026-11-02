package ir.ornix.passgen.core.model.localauth

enum class FailedAuthAttemptLimit(val maxAttempts: Long) {
    DISABLED(Long.MAX_VALUE),
    ONE_ATTEMPT(1L),
    THREE_ATTEMPTS(3L),
    FIVE_ATTEMPTS(5L),
    TEN_ATTEMPTS(10L);


    companion object {
        fun fromMaxAttempts(maxAttempts: Long): FailedAuthAttemptLimit {
            return entries.firstOrNull { it.maxAttempts == maxAttempts }
                ?: DISABLED
        }
    }
}