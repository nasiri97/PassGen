package ir.ornix.passgen.composeapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation3.runtime.NavKey
import org.koin.compose.viewmodel.koinViewModel

class NavViewModelStoreProvider : ViewModel() {
    private val stores = mutableMapOf<NavKey, ViewModelStore>()

    fun getViewModelStoreOwner(key: NavKey): ViewModelStoreOwner {
        val store = stores.getOrPut(key) { ViewModelStore() }
        return object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore = store
        }
    }

    fun clearUnused(activeKeys: Set<NavKey>) {
        val keysToRemove = stores.keys.filter { it !in activeKeys }
        for (key in keysToRemove) {
            stores.remove(key)?.clear()
        }
    }

    override fun onCleared() {
        super.onCleared()
        stores.values.forEach { it.clear() }
        stores.clear()
    }
}

@Composable
fun NavEntryScopedViewModelStore(
    key: NavKey,
    content: @Composable () -> Unit
) {
    val provider: NavViewModelStoreProvider = koinViewModel()
    val storeOwner = remember(key) {
        provider.getViewModelStoreOwner(key)
    }

    CompositionLocalProvider(LocalViewModelStoreOwner provides storeOwner) {
        content()
    }
}

