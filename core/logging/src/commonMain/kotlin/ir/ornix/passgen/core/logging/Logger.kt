package ir.ornix.passgen.core.logging


private const val DEFAULT_TAG = "PassGen"

/**
 * Platform-agnostic Logger interface for PassGen.
 *
 * Provides structured logging functions across Android, iOS, JVM, and Web targets.
 */
expect object Logger {
    fun d(message: String, tag: String = DEFAULT_TAG)
    fun i(message: String, tag: String = DEFAULT_TAG)
    fun w(message: String, tag: String = DEFAULT_TAG)
    fun e(message: String, t: Throwable? = null, tag: String = DEFAULT_TAG)
}
