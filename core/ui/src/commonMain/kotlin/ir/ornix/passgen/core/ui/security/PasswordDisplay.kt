package ir.ornix.passgen.core.ui.security

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import ir.ornix.passgen.core.common.util.withLineBreakOpportunities
import ir.ornix.passgen.core.model.Password
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.action_hide
import ir.ornix.passgen.core.ui.action_show
import ir.ornix.passgen.core.ui.error_password_generation_failed
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.milliseconds

/**
 * Displays a generated password that is masked by default, with a toggle to reveal it
 * and a tap-to-copy action.
 *
 * **Security behavior**
 * - The real password is converted to a `String` only while revealed. While masked,
 *   the mask is built from the length alone.
 * - The password is re-masked when the app leaves the foreground (`ON_STOP`) and after
 *   [revealTimeoutMillis].
 * - The reveal state is not saved across configuration changes or process death.
 * - Zero-width spaces are inserted for line wrapping in the displayed text only.
 *   The array passed to [onCopyRequested] is never modified.
 *
 * **States**
 * - Loading ([isLoading] is true): shows a progress indicator in place of the text.
 * - Error ([password] is null): shows the "generation failed" message in the error color.
 * - Normal: shows the masked or revealed password.
 *
 * @param password The password to display, or null if generation failed.
 * @param onCopyRequested Called with the password when the user taps it.
 * @param isLoading Whether the password is still being generated.
 * @param maskChar Character used to build the mask while hidden.
 * @param maskLength Fixed mask length, which hides the real password length.
 * If null, the mask has the same length as the password.
 * @param revealTimeoutMillis Time in milliseconds before a revealed password is
 * masked again. Use 0 to disable the timeout.
 * @param modifier Modifier applied to the root layout.
 */
@Composable
fun PasswordDisplay(
    password: Password?,
    onCopyRequested: (Password) -> Unit,
    isLoading: Boolean = false,
    maskChar: Char = '•',
    maskLength: Int? = null,
    revealTimeoutMillis: Long = 10_000,
    modifier: Modifier = Modifier
) {

    var revealed by remember { mutableStateOf(false) }

    // Re-mask when the app leaves the foreground
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        revealed = false
    }

    // Auto re-mask after a timeout
    LaunchedEffect(revealed, revealTimeoutMillis) {
        if (revealed && revealTimeoutMillis > 0) {
            delay(revealTimeoutMillis.milliseconds)
            revealed = false
        }
    }

    val displayText: String? = when {
        password == null -> null
        revealed -> {
            val spaced = password.value.withLineBreakOpportunities()
            val text = spaced.concatToString()
            spaced.fill('\u0000') // the caller wipes the returned array
            text
        }

        else -> maskChar.toString().repeat(maskLength ?: password.value.size)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (isLoading) {
            LinearWavyProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                wavelength = 20.dp
            )
        } else {
            Text(
                modifier = Modifier.weight(1f).securePassword()
                    .clickable(enabled = password != null) {
                        password?.let(onCopyRequested)
                    },
                text = displayText ?: stringResource(Res.string.error_password_generation_failed),
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = FontFamily.Monospace,
                color =
                    if (displayText == null) MaterialTheme.colorScheme.error
                    else Color.Unspecified
            )
        }

        IconButton(onClick = { revealed = !revealed }) {
            Icon(
                imageVector =
                    if (revealed) Icons.Default.VisibilityOff
                    else Icons.Default.Visibility,
                contentDescription =
                    if (revealed) stringResource(Res.string.action_hide)
                    else stringResource(Res.string.action_show)
            )
        }
    }
}