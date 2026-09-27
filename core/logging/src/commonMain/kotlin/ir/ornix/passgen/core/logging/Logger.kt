package ir.ornix.passgen.core.logging

expect object Logger {
    fun d(message: String)
    fun i(message: String)
    fun w(message: String)
    fun e(message: String, t: Throwable? = null)
}
