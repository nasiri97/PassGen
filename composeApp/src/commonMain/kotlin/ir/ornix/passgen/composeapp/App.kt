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
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.DrawerValue
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import ir.ornix.passgen.composeapp.di.appModule
import ir.ornix.passgen.core.designsystem.theme.PassGenTheme
import ir.ornix.passgen.core.domain.appconfig.IsFirstLaunchUseCase
import ir.ornix.passgen.core.domain.appconfig.SetFirstLaunchUseCase
import ir.ornix.passgen.core.domain.localauth.IsUnlockingRequiredUseCase
import ir.ornix.passgen.core.ui.security.secureContent
import ir.ornix.passgen.feature.about.api.AboutRoute
import ir.ornix.passgen.feature.about.impl.ui.AboutScreen
import ir.ornix.passgen.feature.config.api.AddConfigRoute
import ir.ornix.passgen.feature.config.api.PassGenConfigType
import ir.ornix.passgen.feature.config.impl.AddConfigScreen
import ir.ornix.passgen.feature.home.api.HomeRoute
import ir.ornix.passgen.feature.home.impl.ui.HomeScreen
import ir.ornix.passgen.feature.localauth.api.LocalAuthRoute
import ir.ornix.passgen.feature.localauth.impl.ui.SecretSetupScreen
import ir.ornix.passgen.feature.random.api.RandomRoute
import ir.ornix.passgen.feature.random.impl.ui.RandomScreen
import ir.ornix.passgen.feature.savedpasswords.api.SavedPasswordsRoute
import ir.ornix.passgen.feature.savedpasswords.impl.ui.SavedPasswordsScreen
import ir.ornix.passgen.feature.settings.api.SettingsRoute
import ir.ornix.passgen.feature.settings.impl.ui.SettingsScreen
import ir.ornix.passgen.feature.unlock.api.UnlockRoute
import ir.ornix.passgen.feature.unlock.impl.ui.UnlockingGateScreen
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.KoinApplication
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

sealed class NavItem(val route: NavKey, val label: String, val icon: ImageVector) {
    data object Home : NavItem(HomeRoute, "Home", Icons.Default.Home)
    data object Random : NavItem(RandomRoute, "Random", Icons.Default.Shuffle)
    data object SavedPasswords : NavItem(SavedPasswordsRoute, "Saved Passwords", Icons.Default.Lock)
    data object Settings : NavItem(SettingsRoute, "Settings", Icons.Default.Settings)
    data object About : NavItem(AboutRoute, "About", Icons.Default.Info)
}

private val drawerItems = listOf(
    NavItem.Home,
    NavItem.Random,
    NavItem.SavedPasswords,
    NavItem.Settings,
    NavItem.About
)

@Composable
fun App(modifier: Modifier = Modifier) {
    KoinApplication(application = { modules(appModule) }) {
        KoinContext {
            val isFirstLaunchUseCase: IsFirstLaunchUseCase = koinInject()
            val setFirstLaunch: SetFirstLaunchUseCase = koinInject()
            val isUnlockingRequiredUseCase: IsUnlockingRequiredUseCase = koinInject()

            val isFirstLaunch = isFirstLaunchUseCase()
            val isUnlockingRequired = isUnlockingRequiredUseCase()

            val initialRoute = when {
                isFirstLaunch -> LocalAuthRoute()
                isUnlockingRequired -> UnlockRoute
                else -> HomeRoute
            }

            val backStack = rememberNavBackStack(
                SavedStateConfiguration {
                    serializersModule = SerializersModule {
                        polymorphic(NavKey::class) {
                            subclass(HomeRoute::class, HomeRoute.serializer())
                            subclass(RandomRoute::class, RandomRoute.serializer())
                            subclass(SavedPasswordsRoute::class, SavedPasswordsRoute.serializer())
                            subclass(SettingsRoute::class, SettingsRoute.serializer())
                            subclass(AboutRoute::class, AboutRoute.serializer())
                            subclass(LocalAuthRoute::class, LocalAuthRoute.serializer())
                            subclass(UnlockRoute::class, UnlockRoute.serializer())
                            subclass(AddConfigRoute::class, AddConfigRoute.serializer())
                        }
                    }
                },
                initialRoute
            )

            val vmStores = remember { mutableMapOf<NavKey, ViewModelStore>() }

            PassGenTheme {
                Surface(
                    modifier = modifier.secureContent().fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavDisplay(
                        backStack = backStack,
                        onBack = {
                            if (backStack.size > 1) {
                                val removed = backStack.removeLastOrNull()
                                (removed as? LocalAuthRoute)?.let { vmStores.remove(it)?.clear() }
                            }
                        },
                        entryProvider = { key ->
                            when (key) {
                                is LocalAuthRoute -> NavEntry(key) {
                                    val store =
                                        remember(key) { vmStores.getOrPut(key) { ViewModelStore() } }
                                    CompositionLocalProvider(
                                        LocalViewModelStoreOwner provides remember(
                                            store
                                        ) {
                                            object : ViewModelStoreOwner {
                                                override val viewModelStore = store
                                            }
                                        }) {
                                        SecretSetupScreen(
                                            onFinished = {
                                                setFirstLaunch(false)
                                                backStack.removeLastOrNull()
                                                if (backStack.isEmpty()) backStack.add(HomeRoute)
                                            }
                                        )
                                    }
                                }

                                is UnlockRoute -> NavEntry(key) {
                                    UnlockingGateScreen(
                                        onUnlocked = {
                                            backStack.clear()
                                            backStack.add(HomeRoute)
                                        }
                                    )
                                }

                                is AddConfigRoute -> NavEntry(key) {
                                    AddConfigScreen(
                                        configType = key.configType,
                                        onNavigateBack = {
                                            backStack.removeLastOrNull()
                                        },
                                        onConfigCreated = {
                                            backStack.removeLastOrNull()
                                        }
                                    )
                                }

                                is HomeRoute,
                                is RandomRoute,
                                is SavedPasswordsRoute,
                                is SettingsRoute,
                                is AboutRoute -> NavEntry(key) {
                                    MainAppContent(
                                        currentRoute = key,
                                        onNavigate = { route, clearBackStack ->
                                            if (key != route) {
                                                if (clearBackStack) backStack.clear()
                                                backStack.add(route)
                                            }
                                        },
                                        onNavigateToCreateConfig = { configType ->
                                            backStack.add(AddConfigRoute(configType))
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

@Composable
private fun MainAppContent(
    currentRoute: NavKey,
    onNavigate: (destination: NavKey, clearBackStack: Boolean) -> Unit,
    onNavigateToCreateConfig: (configType: PassGenConfigType) -> Unit,
    modifier: Modifier = Modifier
) {
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(12.dp))

                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigate(item.route, true)
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = drawerItems
                                .find { it.route == currentRoute }
                                ?.label
                                ?: "PassGen"
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    }
                )
            },
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                when (currentRoute) {
                    HomeRoute -> HomeScreen(onNavigateToCreateConfig = {
                        onNavigateToCreateConfig(PassGenConfigType.PASS_GEN_CONFIG_KDF)
                    })

                    RandomRoute -> RandomScreen(onNavigateToCreateConfig = {
                        onNavigateToCreateConfig(PassGenConfigType.PASS_GEN_CONFIG_RANDOM)
                    })

                    SavedPasswordsRoute -> SavedPasswordsScreen()
                    SettingsRoute -> SettingsScreen(
                        onNavigateToSecretSetupClick = {
                            scope.launch { drawerState.close() }
                            onNavigate(LocalAuthRoute(), false)
                        }
                    )

                    AboutRoute -> AboutScreen()
                }
            }
        }
    }
}