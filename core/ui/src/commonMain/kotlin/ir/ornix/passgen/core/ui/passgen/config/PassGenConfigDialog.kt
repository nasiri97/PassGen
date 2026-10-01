package ir.ornix.passgen.core.ui.passgen.config

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.ornix.passgen.core.model.passgenconfig.KdfPassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.PassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.RandomPassGenConfig

@Composable
fun PassGenConfigDialog(
    config: PassGenConfig,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (config) {
        is KdfPassGenConfig -> KdfPassGenConfigDialog(
            config = config,
            onDismiss = onDismiss,
            modifier = modifier
        )

        is RandomPassGenConfig -> RandomPassGenConfigDialog(
            config = config,
            onDismiss = onDismiss,
            modifier = modifier
        )
    }
}
