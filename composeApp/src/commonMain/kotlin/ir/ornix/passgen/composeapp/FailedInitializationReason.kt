package ir.ornix.passgen.composeapp

import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.unsupported_initialization_failed_message
import ir.ornix.passgen.core.ui.unsupported_root_detected_message
import org.jetbrains.compose.resources.StringResource

enum class FailedInitializationReason(val message: StringResource) {
    ROOTED(Res.string.unsupported_root_detected_message),
    INITIALIZATION_FAILED(Res.string.unsupported_initialization_failed_message)
}