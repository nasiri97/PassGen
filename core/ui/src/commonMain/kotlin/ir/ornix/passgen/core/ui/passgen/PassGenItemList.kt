package ir.ornix.passgen.core.ui.passgen

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
import ir.ornix.passgen.core.model.Account
import ir.ornix.passgen.core.model.PassGenItem
import ir.ornix.passgen.core.model.passgenconfig.PassGenConfig
import ir.ornix.passgen.core.ui.component.getAdaptiveValue
import ir.ornix.passgen.core.ui.passgen.config.PassGenConfigDialog

@Composable
fun PassGenItemList(
    passGenItems: List<PassGenItem>,
    removePasswordItem: (PassGenItem) -> Unit,
    addNewAccount: (account: Account) -> Unit,
    copy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var passGenConfig by remember { mutableStateOf<PassGenConfig?>(null) }

    val columnsCount = getAdaptiveValue(
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
        items(items = passGenItems, key = { it.config.id }) { passwordItem ->
            PassGenItemCard(
                passGenItem = passwordItem,
                onRemove = { removePasswordItem(passwordItem) },
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
