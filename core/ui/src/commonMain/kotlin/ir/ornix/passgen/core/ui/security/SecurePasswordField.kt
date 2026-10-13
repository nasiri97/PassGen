package ir.ornix.passgen.core.ui.security

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedSecureTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.action_hide_password
import ir.ornix.passgen.core.ui.action_show_password
import ir.ornix.passgen.core.ui.password
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SecurePasswordField(
    onValueChange: (CharArray) -> Unit,
    revealTimeoutMillis: Long,   // auto re-mask after this long; 0 = no timeout
    modifier: Modifier = Modifier,
    label: @Composable (TextFieldLabelScope.() -> Unit)? = null,
    contentDescription: String? = null,
    supportingText: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    maxLength: Int = 128,
    allowReveal: Boolean = true,
    clearOnStop: Boolean = false,        // wipe when the app goes to background
    clearOnDispose: Boolean = false,     // wipe when the field leaves composition
    testTag: String? = null,
    onDone: () -> Unit = {},
) {
    // Plain remember, NOT rememberTextFieldState(): the latter is savable
    // and would write the password into the saved-instance-state Bundle.
    val state = remember { TextFieldState("") }
    var revealed by remember { mutableStateOf(false) }

    val currentOnValueChange by rememberUpdatedState(onValueChange)

    // field -> caller
    LaunchedEffect(state) {
        snapshotFlow { state.text.toString() }.collect { currentOnValueChange(it.toCharArray()) }
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
                currentOnValueChange(CharArray(0))
            }
        }
    }

    val secureModifier =
        modifier.secureInput(contentDescription ?: stringResource(Res.string.password))

    OutlinedSecureTextField(
        state = state,
        modifier = testTag?.let { secureModifier.testTag(testTag) } ?: secureModifier,
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
                        contentDescription = if (revealed) stringResource(Res.string.action_hide_password) else stringResource(
                            Res.string.action_show_password
                        ),
                    )
                }
            }
        } else null,
    )
}
