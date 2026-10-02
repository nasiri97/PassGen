package ir.ornix.passgen.composeapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation3.runtime.NavKey

@Composable
fun NavEntryScopedViewModelStore(
    key: NavKey,
    content: @Composable () -> Unit
) {
    val storeOwner = remember(key) {
        val store = ViewModelStore()
        object : ViewModelStoreOwner {
            override val viewModelStore = store
        }
    }

    DisposableEffect(key) {
        onDispose {
            storeOwner.viewModelStore.clear()
        }
    }

    CompositionLocalProvider(LocalViewModelStoreOwner provides storeOwner) {
        content()
    }
}
