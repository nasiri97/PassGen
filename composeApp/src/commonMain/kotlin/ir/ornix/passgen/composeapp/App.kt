package ir.ornix.passgen.composeapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ir.ornix.passgen.composeapp.di.appModule
import ir.ornix.passgen.feature.about.api.AboutRoute
import ir.ornix.passgen.feature.about.impl.ui.AboutScreen
import ir.ornix.passgen.feature.home.api.HomeRoute
import ir.ornix.passgen.feature.home.impl.ui.HomeScreen
import ir.ornix.passgen.feature.savedpasswords.api.SavedPasswordsRoute
import ir.ornix.passgen.feature.savedpasswords.impl.ui.SavedPasswordsScreen
import ir.ornix.passgen.feature.settings.api.SettingsRoute
import ir.ornix.passgen.feature.settings.impl.ui.SettingsScreen
import ir.ornix.passgen.feature.setup.api.SetupRoute
import ir.ornix.passgen.feature.setup.impl.ui.SetupScreen
import kotlinx.coroutines.launch
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.runtime.NavKey
import ir.ornix.passgen.core.domain.MasterKeyRepository
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
            val repository: MasterKeyRepository = koinInject()
            val masterKey by repository.getMasterKey().collectAsState(initial = null)
            val isMasterKeySet = masterKey != null

            val backStack = remember { mutableStateListOf<NavKey>() }
            
            LaunchedEffect(isMasterKeySet) {
                if (backStack.isEmpty()) {
                    backStack.add(if (isMasterKeySet) HomeRoute else SetupRoute)
                } else if (!isMasterKeySet && backStack.lastOrNull() != SetupRoute) {
                    backStack.clear()
                    backStack.add(SetupRoute)
                }
            }

            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            val currentRoute = backStack.lastOrNull()
            val showDrawer = currentRoute != null && currentRoute != SetupRoute

            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                if (showDrawer) {
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
                                    title = { Text(drawerItems.find { it.route == currentRoute }?.label ?: "PassGen") },
                                    navigationIcon = {
                                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                                        }
                                    },
                                    actions = {
                                        if (currentRoute == HomeRoute) {
                                            IconButton(onClick = { scope.launch { repository.clearMasterKey() } }) {
                                                Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                                            }
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
                                            is SetupRoute -> NavEntry(key) {
                                                SetupScreen(
                                                    onSetupComplete = {
                                                        backStack.clear()
                                                        backStack.add(HomeRoute)
                                                    }
                                                )
                                            }
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
                } else if (currentRoute != null) {
                    NavDisplay(
                        backStack = backStack,
                        onBack = { if (backStack.size > 1) backStack.removeLast() },
                        entryProvider = { key ->
                            when (key) {
                                is SetupRoute -> NavEntry(key) {
                                    SetupScreen(
                                        onSetupComplete = {
                                            backStack.clear()
                                            backStack.add(HomeRoute)
                                        }
                                    )
                                }
                                else -> NavEntry(key) { Text("Unknown Route") }
                            }
                        }
                    )
                }
            }
        }
    }
}
