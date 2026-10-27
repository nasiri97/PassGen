package ir.ornix.passgen.core.logging

import platform.Foundation.NSLog

actual object Logger {
    actual fun d(message: String, tag: String) {
        NSLog("[$tag] [DEBUG] %s", message)
    }

    actual fun i(message: String, tag: String) {
        NSLog("[$tag] [INFO] %s", message)
    }

    actual fun w(message: String, tag: String) {
        NSLog("[$tag] [WARN] %s", message)
    }

    actual fun e(message: String, t: Throwable?, tag: String) {
        if (t != null) {
            NSLog("[$tag] [ERROR] %s\n%s", message, t.stackTraceToString())
        } else {
            NSLog("[$tag] [ERROR] %s", message)
        }
    }
}
