package ir.ornix.passgen.feature.config.impl

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
    modifier: Modifier = Modifier
) {
    when (configType) {
        PassGenConfigType.PASS_GEN_CONFIG_KDF ->
            AddKdfConfigScreen(
                onNavigateBack = onNavigateBack,
                onConfigCreated = onConfigCreated,
                modifier = modifier
            )

        PassGenConfigType.PASS_GEN_CONFIG_RANDOM ->
            AddRandomConfigScreen(
                onNavigateBack = onNavigateBack,
                onConfigCreated = onConfigCreated,
                modifier = modifier
            )
    }
}