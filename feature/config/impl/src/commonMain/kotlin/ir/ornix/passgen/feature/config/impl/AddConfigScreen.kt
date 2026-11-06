package ir.ornix.passgen.feature.config.impl

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.ornix.passgen.feature.config.api.PassGenConfigType
import ir.ornix.passgen.feature.config.impl.kdf.ui.AddKdfConfigScreen
import ir.ornix.passgen.feature.config.impl.random.ui.AddRandomConfigScreen


@Composable
fun AddConfigScreen(
    configType: PassGenConfigType,
    onNavigateBack: () -> Unit,
    onConfigCreated: () -> Unit,
    onConfigCreationFailed: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (configType) {
        PassGenConfigType.PASS_GEN_CONFIG_KDF ->
            AddKdfConfigScreen(
                modifier = modifier.fillMaxSize(),
                onNavigateBack = onNavigateBack,
                onConfigCreated = onConfigCreated,
                onConfigCreationFailed = onConfigCreationFailed
            )

        PassGenConfigType.PASS_GEN_CONFIG_RANDOM ->
            AddRandomConfigScreen(
                modifier = modifier.fillMaxSize(),
                onNavigateBack = onNavigateBack,
                onConfigCreated = onConfigCreated,
                onConfigCreationFailed = onConfigCreationFailed
            )
    }
}