package ir.ornix.passgen.core.logging

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

actual object Logger {
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

    actual fun d(message: String, tag: String) {
        log("DEBUG", tag, message)
    }

    actual fun i(message: String, tag: String) {
        log("INFO", tag, message)
    }

    actual fun w(message: String, tag: String) {
        log("WARN", tag, message)
    }

    actual fun e(message: String, t: Throwable?, tag: String) {
        log("ERROR", tag, message, isError = true)
        t?.printStackTrace()
    }

    private fun log(level: String, tag: String, message: String, isError: Boolean = false) {
        val timestamp = LocalDateTime.now().format(timeFormatter)
        val formatted = "$timestamp [$level] $tag: $message"
        if (isError) {
            System.err.println(formatted)
        } else {
            println(formatted)
        }
    }
}
