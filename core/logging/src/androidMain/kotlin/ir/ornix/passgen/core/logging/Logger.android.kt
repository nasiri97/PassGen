package ir.ornix.passgen.core.logging

import android.util.Log

actual object Logger {
    actual fun d(message: String, tag: String) {
        Log.d(tag, message)
    }

    actual fun i(message: String, tag: String) {
        Log.i(tag, message)
    }

    actual fun w(message: String, tag: String) {
        Log.w(tag, message)
    }

    actual fun e(message: String, t: Throwable?, tag: String) {
        if (t != null) {
            Log.e(tag, message, t)
        } else {
            Log.e(tag, message)
        }
    }
}
