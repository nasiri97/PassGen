package ir.ornix.passgen.logcore

actual object Logger {
    actual fun d(message: String) {
        logToConsole(message)
    }

    actual fun i(message: String) {
        infoToConsole(message)
    }

    actual fun w(message: String) {
        warnToConsole(message)
    }

    actual fun e(message: String, t: Throwable?) {
        errorToConsole(message, t?.toString() ?: "")
    }
}

@JsFun("(msg) => console.log(msg)")
private external fun logToConsole(msg: String)

@JsFun("(msg) => console.info(msg)")
private external fun infoToConsole(msg: String)

@JsFun("(msg) => console.warn(msg)")
private external fun warnToConsole(msg: String)

@JsFun("(msg, err) => console.error(msg, err)")
private external fun errorToConsole(msg: String, err: String)
