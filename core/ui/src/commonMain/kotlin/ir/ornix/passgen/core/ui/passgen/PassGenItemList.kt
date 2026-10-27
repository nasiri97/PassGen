package ir.ornix.passgen.core.ui.passgen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import ir.ornix.passgen.core.model.Credential
import ir.ornix.passgen.core.model.PassGenItem
import ir.ornix.passgen.core.model.Password
import ir.ornix.passgen.core.model.passgenconfig.PassGenConfig

@Composable
fun PassGenItemList(
    passGenItems: List<PassGenItem>,
    removePasswordItem: (PassGenItem) -> Unit,
    addNewCredential: (credential: Credential) -> Unit,
    onCopyRequested: (Password) -> Unit,
    revealTimeoutMillis: Long,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState? = null,
    onSnackbarAvailabilityChanged: (Boolean) -> Unit = {}
) {
    var passGenConfig by remember { mutableStateOf<PassGenConfig?>(null) }

    LazyVerticalStaggeredGrid(
        modifier = modifier.testTag("pass_gen_items_container"),
        columns = StaggeredGridCells.Adaptive(minSize = 320.dp),
        verticalItemSpacing = 16.dp,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        items(items = passGenItems, key = { it.config.id }) { passwordItem ->
            PassGenItemCard(
                passGenItem = passwordItem,
                onRemove = { removePasswordItem(passwordItem) },
                addNewCredential = addNewCredential,
                showPassGenInfoDialog = { passGenConfig = it },
                onCopyRequested = onCopyRequested,
                revealTimeoutMillis = revealTimeoutMillis,
                snackbarHostState = snackbarHostState,
                onSnackbarAvailabilityChanged = onSnackbarAvailabilityChanged
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
