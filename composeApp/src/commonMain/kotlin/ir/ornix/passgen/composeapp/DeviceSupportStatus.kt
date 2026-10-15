package ir.ornix.passgen.composeapp

import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.root_detected_message
import ir.ornix.passgen.core.ui.supported_message
import ir.ornix.passgen.core.ui.unsupported_message
import ir.ornix.passgen.core.ui.unsupported_os_message
import org.jetbrains.compose.resources.StringResource

/**
 * Represents whether the device/app environment is supported.
 * Each status maps to a string resource when applicable.
 */
enum class DeviceSupportStatus(val message: StringResource) {
    SUPPORTED(Res.string.supported_message),
    GENERAL(Res.string.unsupported_message),
    ROOTED(Res.string.root_detected_message),
    OLD_OS(Res.string.unsupported_os_message)
}