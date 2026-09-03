package ir.ornix.passgen.composeapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.russhwolf.multiplatform.settings.Settings
import ir.ornix.passgen.feature.about.api.AboutRoute
import ir.ornix.passgen.feature.about.impl.AboutScreen
import ir.ornix.passgen.feature.home.api.HomeRoute
import ir.ornix.passgen.feature.home.impl.HomeScreen
import ir.ornix.passgen.feature.savedpasswords.api.SavedPasswordsRoute
import ir.ornix.passgen.feature.savedpasswords.impl.SavedPasswordsScreen
import ir.ornix.passgen.feature.settings.api.SettingsRoute
import ir.ornix.passgen.feature.settings.impl.SettingsScreen
import ir.ornix.passgen.feature.setup.api.SetupRoute
import ir.ornix.passgen.feature.setup.impl.SetupScreen
import kotlinx.coroutines.launch
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.runtime.NavKey

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
    val settings = remember { Settings() }
    val hasMasterKey = remember { settings.hasKey("master_key") }
    
    val initialRoute: NavKey = if (hasMasterKey) HomeRoute else SetupRoute
    val backStack = remember { mutableStateListOf<NavKey>(initialRoute) }
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val currentRoute = backStack.lastOrNull()
    val showDrawer = currentRoute != SetupRoute

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
                                        SetupScreen(onSetupComplete = {
                                            backStack.clear()
                                            backStack.add(HomeRoute)
                                        })
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
        } else {
            NavDisplay(
                backStack = backStack,
                onBack = { if (backStack.size > 1) backStack.removeLast() },
                entryProvider = { key ->
                    when (key) {
                        is SetupRoute -> NavEntry(key) {
                            SetupScreen(onSetupComplete = {
                                backStack.clear()
                                backStack.add(HomeRoute)
                            })
                        }
                        else -> NavEntry(key) { Text("Unknown Route") }
                    }
                }
            )
        }
    }
}
