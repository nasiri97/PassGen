package ir.ornix.passgen.core.ui.security


import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldLabelScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import ir.ornix.passgen.core.common.isDebugBuild
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun SecurePasswordField(
    value: ByteArray,
    onValueChange: (ByteArray) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (TextFieldLabelScope.() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    maxLength: Int = 128,
    allowReveal: Boolean = true,
    revealTimeoutMillis: Long = 5_000,   // auto re-mask after this long; 0 = no timeout
    clearOnStop: Boolean = false,        // wipe when the app goes to background
    clearOnDispose: Boolean = false,     // wipe when the field leaves composition
    blockScreenCapture: Boolean = true,  // FLAG_SECURE on Android
    contentType: ContentType = ContentType.Password,
    testTag: String? = null,
    onDone: () -> Unit = {},
) {
    if (isDebugBuild) {
        OutlinedTextField(
            modifier = testTag?.let { modifier.testTag(testTag) } ?: modifier,
            value = value.decodeToString(),
            onValueChange = { onValueChange(it.encodeToByteArray()) },
            label = {
                Text(
                    "Debugging mod: It is not safe!",
                    color = MaterialTheme.colorScheme.error
                )
            },
            supportingText = supportingText,
            enabled = enabled,
            isError = isError
        )
    } else {
        ReleaseSecurePasswordField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.secureContent(),
            label = label,
            supportingText = supportingText,
            enabled = enabled,
            isError = isError,
            maxLength = maxLength,
            allowReveal = allowReveal,
            revealTimeoutMillis = revealTimeoutMillis,
            clearOnStop = clearOnStop,
            clearOnDispose = clearOnDispose,
            blockScreenCapture = blockScreenCapture,
            contentType = contentType,
            onDone = onDone
        )
    }
}


@Composable
fun ReleaseSecurePasswordField(
    value: ByteArray,
    onValueChange: (ByteArray) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (TextFieldLabelScope.() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    maxLength: Int = 128,
    allowReveal: Boolean = true,
    revealTimeoutMillis: Long = 5_000,   // auto re-mask after this long; 0 = no timeout
    clearOnStop: Boolean = false,        // wipe when the app goes to background
    clearOnDispose: Boolean = false,     // wipe when the field leaves composition
    blockScreenCapture: Boolean = true,  // FLAG_SECURE on Android
    contentType: ContentType = ContentType.Password,
    onDone: () -> Unit = {},
) {
    // Plain remember, NOT rememberTextFieldState(): the latter is savable
    // and would write the password into the saved-instance-state Bundle.
    val state = remember { TextFieldState(value.decodeToString()) }
    var revealed by remember { mutableStateOf(false) }

    val currentOnValueChange by rememberUpdatedState(onValueChange)

    // field -> caller
    LaunchedEffect(state) {
        snapshotFlow { state.text.toString() }
            .collect { currentOnValueChange(it.encodeToByteArray()) }
    }

    // caller -> field (e.g. the caller clears the value after submit)
    LaunchedEffect(value) {
        val mkStr = value.decodeToString()
        if (mkStr != state.text.toString()) state.setTextAndPlaceCursorAtEnd(mkStr)
    }

    // Re-mask (and optionally wipe) when leaving the foreground
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        revealed = false
        if (clearOnStop) state.clearText()
    }

    // Auto re-mask after a timeout
    LaunchedEffect(revealed, revealTimeoutMillis) {
        if (revealed && revealTimeoutMillis > 0) {
            delay(revealTimeoutMillis.milliseconds)
            revealed = false
        }
    }

    // Wipe on dispose. The collector is cancelled by then, so notify the caller explicitly.
    if (clearOnDispose) {
        DisposableEffect(state) {
            onDispose {
                state.clearText()
                currentOnValueChange(ByteArray(0))
            }
        }
    }

    //ScreenCaptureProtection(enabled = blockScreenCapture)

    OutlinedSecureTextField(
        state = state,
        modifier = modifier.semantics {
            this.contentType = contentType
        }, // password-manager autofill
        enabled = enabled,
        label = label,
        supportingText = supportingText,
        isError = isError,
        inputTransformation = InputTransformation.maxLength(maxLength),
        textObfuscationMode = if (revealed) TextObfuscationMode.Visible
        else TextObfuscationMode.Hidden,   // no "last typed" flash
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            autoCorrectEnabled = false,
            capitalization = KeyboardCapitalization.None,
            imeAction = ImeAction.Done,
        ),
        onKeyboardAction = { onDone() },
        trailingIcon = if (allowReveal) {
            {
                IconButton(onClick = { revealed = !revealed }) {
                    Icon(
                        imageVector = if (revealed) Icons.Default.VisibilityOff
                        else Icons.Default.Visibility,
                        contentDescription = if (revealed) "Hide password" else "Show password",
                    )
                }
            }
        } else null,
    )
}