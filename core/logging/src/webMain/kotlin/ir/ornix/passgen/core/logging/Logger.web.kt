package ir.ornix.passgen.core.logging

actual object Logger {
    actual fun d(message: String, tag: String) {
        consoleLog("[$tag] [DEBUG] $message")
    }

    actual fun i(message: String, tag: String) {
        consoleInfo("[$tag] [INFO] $message")
    }

    actual fun w(message: String, tag: String) {
        consoleWarn("[$tag] [WARN] $message")
    }

    actual fun e(message: String, t: Throwable?, tag: String) {
        val fullMessage = if (t != null) {
            "[$tag] [ERROR] $message\n${t.stackTraceToString()}"
        } else {
            "[$tag] [ERROR] $message"
        }
        consoleError(fullMessage)
    }
}

@JsFun("(msg) => console.log(msg)")
private external fun consoleLog(msg: String)

@JsFun("(msg) => console.info(msg)")
private external fun consoleInfo(msg: String)

@JsFun("(msg) => console.warn(msg)")
private external fun consoleWarn(msg: String)

@JsFun("(msg) => console.error(msg)")
private external fun consoleError(msg: String)
