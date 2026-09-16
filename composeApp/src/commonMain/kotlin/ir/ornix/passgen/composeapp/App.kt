package ir.ornix.passgen.composeapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import ir.ornix.passgen.composeapp.di.appModule
import ir.ornix.passgen.core.domain.appconfig.IsFirstLaunchUseCase
import ir.ornix.passgen.core.domain.localauth.IsUnlockingRequiredUseCase
import ir.ornix.passgen.feature.about.api.AboutRoute
import ir.ornix.passgen.feature.about.impl.ui.AboutScreen
import ir.ornix.passgen.feature.home.api.HomeRoute
import ir.ornix.passgen.feature.home.impl.ui.HomeScreen
import ir.ornix.passgen.feature.localauth.impl.secretsetup.SecretSetupScreen
import ir.ornix.passgen.feature.localauth.impl.unlocking.UnlockingGateScreen
import ir.ornix.passgen.feature.savedpasswords.api.SavedPasswordsRoute
import ir.ornix.passgen.feature.savedpasswords.impl.ui.SavedPasswordsScreen
import ir.ornix.passgen.feature.settings.api.SettingsRoute
import ir.ornix.passgen.feature.settings.impl.ui.SettingsScreen
import kotlinx.coroutines.launch
import org.koin.compose.KoinApplication
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

sealed class NavItem(val route: NavKey, val label: String, val icon: ImageVector) {
    data object Home : NavItem(HomeRoute, "Home", Icons.Default.Home)
    data object SavedPasswords : NavItem(SavedPasswordsRoute, "Saved Passwords", Icons.Default.Lock)
    data object Settings : NavItem(SettingsRoute, "Settings", Icons.Default.Settings)
    data object About : NavItem(AboutRoute, "About", Icons.Default.Info)
}

val drawerItems = listOf(
    NavItem.Home,
    NavItem.SavedPasswords,
    NavItem.Settings,
    NavItem.About
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    KoinApplication(application = {
        modules(appModule)
    }) {
        KoinContext {
            val backStack = remember { mutableStateListOf<NavKey>(HomeRoute) }

            val isFirstLaunch: IsFirstLaunchUseCase = koinInject()
            val isUnlockingRequired: IsUnlockingRequiredUseCase = koinInject()

            var isSettingSecretRequired by remember {
                mutableStateOf(isFirstLaunch())
            }

            var isLocked by remember {
                mutableStateOf(isUnlockingRequired())
            }

            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            val currentRoute = backStack.lastOrNull()

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                if (isSettingSecretRequired) {
                    SecretSetupScreen()
                } else if (isLocked) {
                    UnlockingGateScreen(onUnlocked = { isLocked = false })
                } else {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet {
                                Spacer(Modifier.height(12.dp))
                                drawerItems.forEach { item ->
                                    NavigationDrawerItem(
                                        label = { Text(item.label) },
                                        selected = currentRoute == item.route,
                                        onClick = {
                                            scope.launch { drawerState.close() }
                                            if (currentRoute != item.route) {
                                                backStack.clear()
                                                backStack.add(item.route)
                                            }
                                        },
                                        icon = { Icon(item.icon, contentDescription = null) },
                                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                    )
                                }
                            }
                        }
                    ) {
                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    title = {
                                        Text(
                                            drawerItems.find { it.route == currentRoute }?.label
                                                ?: "PassGen"
                                        )
                                    },
                                    navigationIcon = {
                                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                                        }
                                    }
                                )
                            }
                        ) { innerPadding ->
                            Box(modifier = Modifier.padding(innerPadding)) {
                                NavDisplay(
                                    backStack = backStack,
                                    onBack = { if (backStack.size > 1) backStack.removeLast() },
                                    entryProvider = { key ->
                                        when (key) {
                                            is HomeRoute -> NavEntry(key) { HomeScreen() }
                                            is AboutRoute -> NavEntry(key) { AboutScreen() }
                                            is SavedPasswordsRoute -> NavEntry(key) { SavedPasswordsScreen() }
                                            is SettingsRoute -> NavEntry(key) { SettingsScreen() }
                                            else -> NavEntry(key) { Text("Unknown Route") }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}