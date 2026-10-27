package ir.ornix.passgen.core.ui.security

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
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
import ir.ornix.passgen.core.ui.passgen.PasswordGeneratingIndicator
import ir.ornix.passgen.core.ui.password
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
 *
 * **States**
 * - Loading ([isLoading] is true): shows a progress indicator in place of the text.
 * - Error ([password] is null): shows the "generation failed" message in the error color.
 * - Normal: shows the masked or revealed password.
 *
 * @param password The password to display, or null if generation failed.
 * @param revealTimeoutMillis Time in milliseconds before a revealed password is
 * masked again. Use 0 to disable the timeout.
 * @param isLoading Whether the password is still being generated.
 * @param maskChar Character used to build the mask while hidden.
 * @param maskLength Fixed mask length, which hides the real password length.
 * If null, the mask has the same length as the password.
 * @param modifier Modifier applied to the root layout.
 */
@Composable
fun PasswordDisplay(
    password: Password?,
    revealTimeoutMillis: Long,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    maskChar: Char = '•',
    maskLength: Int? = null,
    isClickable: Boolean = false,
    onClick: () -> Unit = {}
) {

    var revealed by rememberSaveable { mutableStateOf(false) }

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

    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
    ) {

        if (isLoading) {
            PasswordGeneratingIndicator(Modifier.fillMaxWidth().height(64.dp))
        } else {
            Row(
                modifier = Modifier
                    .clickable(enabled = isClickable) { onClick() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.weight(1f)
                        .secureContent(stringResource(Res.string.password)),
                    text = displayText
                        ?: stringResource(Res.string.error_password_generation_failed),
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = FontFamily.Monospace,
                    color =
                        if (displayText == null) MaterialTheme.colorScheme.error
                        else Color.Unspecified
                )


                IconButton(
                    onClick = { revealed = !revealed },
                    modifier = Modifier.testTag("password_visibility_toggle")
                ) {
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
    }
}