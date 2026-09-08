package ir.ornix.passgen.feature.home.impl.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.ornix.passgen.core.designsystem.component.getAdaptiveValue
import ir.ornix.passgen.core.domain.model.Account
import ir.ornix.passgen.feature.home.impl.model.PassGenWrapper
import ir.ornix.passgen.passwordgenerator.kdf.KDFPassGenConfig

@Composable
fun PasswordGeneratorList(
    passGenWrappers: List<PassGenWrapper>,
    removeConfig: (PassGenWrapper) -> Unit,
    addNewAccount: (account: Account) -> Unit,
    copy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var passGenConfig by remember { mutableStateOf<KDFPassGenConfig?>(null) }

    val columnsCount = getAdaptiveValue<Int>(
        compact = { 1 },
        expanded = { 2 }
    )

    LazyVerticalStaggeredGrid(
        modifier = modifier,
        columns = StaggeredGridCells.Fixed(columnsCount),
        verticalItemSpacing = 16.dp,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        items(items = passGenWrappers, key = { it.passGen.passGenConfig.id }) { passGenWrapper ->
            val password by passGenWrapper.password.collectAsStateWithLifecycle()
            val isCalculating by passGenWrapper.isCalculating.collectAsStateWithLifecycle()
            
            SwipeablePasswordGenerator(
                passGenConfig = passGenWrapper.passGen.passGenConfig,
                password = password,
                isLoading = isCalculating,
                onRemove = { removeConfig(passGenWrapper) },
                addNewAccount = addNewAccount,
                showPassGenInfoDialog = { passGenConfig = it },
                copy = copy
            )
        }
    }

    passGenConfig?.let {
        PassGenConfigDialog(
            config = it,
            onDismiss = { passGenConfig = null }
        )
    }
}
