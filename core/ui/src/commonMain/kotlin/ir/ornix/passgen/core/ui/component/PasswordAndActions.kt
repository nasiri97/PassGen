package ir.ornix.passgen.core.ui.component

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import ir.ornix.passgen.core.model.Password
import ir.ornix.passgen.core.ui.security.securePassword
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun PasswordAndActions(
    password: Password?,
    onCopyRequested: (Password) -> Unit,
    isLoading: Boolean = false,
    maskChar: Char = '•',
    maskLength: Int? = null,                 // fixed mask length hides the real length; null = real length
    revealTimeoutMillis: Long = 10_000,      // auto re-mask; 0 = no timeout
    modifier: Modifier = Modifier
) {

    var revealed by remember { mutableStateOf(false) }

    // Re-mask when the app leaves the foreground
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) { revealed = false }

    // Auto re-mask after a timeout
    LaunchedEffect(revealed, revealTimeoutMillis) {
        if (revealed && revealTimeoutMillis > 0) {
            delay(revealTimeoutMillis.milliseconds)
            revealed = false
        }
    }

    val passwordCharArray = password?.value

    // The real String exists only while revealed; when hidden, the mask is built from the length alone
    val shown: String = remember(revealed, passwordCharArray.contentHashCode()) {
        if (passwordCharArray == null) "**********"
        else if (revealed) passwordCharArray.concatToString()
        else maskChar.toString().repeat(maskLength ?: passwordCharArray.size)
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
                text = shown,
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(1f).securePassword().clickable {
                    password?.let(onCopyRequested)
                }
            )
        }

        IconButton(onClick = { revealed = !revealed }) {
            Icon(
                imageVector = if (revealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = if (revealed) "Hide" else "Show",
            )
        }
    }
}
