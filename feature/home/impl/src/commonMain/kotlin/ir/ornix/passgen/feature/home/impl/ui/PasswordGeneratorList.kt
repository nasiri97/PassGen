package ir.ornix.passgen.feature.home.impl.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.ornix.passgen.core.domain.passgen.PassGenWrapper
import ir.ornix.passgen.core.domain.passgenconfig.model.KDFPassGenConfig
import ir.ornix.passgen.core.model.Account
import ir.ornix.passgen.core.ui.component.getAdaptiveValue

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
        items(items = passGenWrappers, key = { it.passGenConfig.id }) { passGenWrapper ->
            val password by passGenWrapper.password.collectAsStateWithLifecycle(initialValue = null)
            val isCalculating by passGenWrapper.isCalculating.collectAsStateWithLifecycle()

            SwipeablePasswordGenerator(
                passGenConfig = passGenWrapper.passGenConfig,
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
