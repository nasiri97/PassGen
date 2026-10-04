package ir.ornix.passgen.composeapp

import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLocaleList
import androidx.compose.ui.platform.LocalProvidableLocaleList
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import ir.ornix.passgen.composeapp.di.appModule
import ir.ornix.passgen.composeapp.navigation.NavEntryScopedViewModelStore
import ir.ornix.passgen.composeapp.navigation.appNavConfiguration
import ir.ornix.passgen.core.designsystem.theme.PassGenTheme
import ir.ornix.passgen.core.domain.appconfig.GetLanguageUseCase
import ir.ornix.passgen.core.domain.appconfig.GetThemeUseCase
import ir.ornix.passgen.core.domain.appconfig.IsFirstLaunchUseCase
import ir.ornix.passgen.core.domain.appconfig.SetFirstLaunchUseCase
import ir.ornix.passgen.core.domain.localauth.IsUnlockingRequiredUseCase
import ir.ornix.passgen.core.model.AppLanguage
import ir.ornix.passgen.core.model.AppTheme
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.app_name
import ir.ornix.passgen.core.ui.content_desc_menu
import ir.ornix.passgen.core.ui.nav_about
import ir.ornix.passgen.core.ui.nav_home
import ir.ornix.passgen.core.ui.nav_random
import ir.ornix.passgen.core.ui.nav_saved_passwords
import ir.ornix.passgen.core.ui.nav_settings
import ir.ornix.passgen.core.ui.security.secureContent
import ir.ornix.passgen.core.ui.util.setAppLocale
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
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.koinConfiguration

data class NavItem(val route: NavKey, val label: String, val icon: ImageVector)

@Composable
fun App(modifier: Modifier = Modifier) {
    KoinApplication(
        configuration = koinConfiguration {
            modules(appModule)
        }
    ) {
        val isFirstLaunchUseCase: IsFirstLaunchUseCase = koinInject()
        val setFirstLaunch: SetFirstLaunchUseCase = koinInject()
        val isUnlockingRequiredUseCase: IsUnlockingRequiredUseCase = koinInject()
        val getLanguageUseCase: GetLanguageUseCase = koinInject()
        val getThemeUseCase: GetThemeUseCase = koinInject()

        val isFirstLaunch = isFirstLaunchUseCase()
        val isUnlockingRequired = isUnlockingRequiredUseCase()

        val selectedLanguage by getLanguageUseCase().collectAsStateWithLifecycle()
        val selectedTheme by getThemeUseCase().collectAsStateWithLifecycle()

        // Apply locale synchronously during composition so string resources resolve immediately
        setAppLocale(selectedLanguage)

        val isDarkTheme = when (selectedTheme) {
            AppTheme.SYSTEM -> isSystemInDarkTheme()
            AppTheme.LIGHT -> false
            AppTheme.DARK -> true
        }

        val systemLocaleList = LocalLocaleList.current
        val localeList = when (selectedLanguage) {
            AppLanguage.SYSTEM -> systemLocaleList
            AppLanguage.ENGLISH -> LocaleList(Locale("en"))
            AppLanguage.PERSIAN -> LocaleList(Locale("fa"))
        }

        val activeLanguageCode = when (selectedLanguage) {
            AppLanguage.SYSTEM -> systemLocaleList.firstOrNull()?.language?.lowercase() ?: "en"
            AppLanguage.ENGLISH -> "en"
            AppLanguage.PERSIAN -> "fa"
        }

        val layoutDirection = if (activeLanguageCode in listOf("fa", "ar", "he", "ur")) {
            LayoutDirection.Rtl
        } else {
            LayoutDirection.Ltr
        }

        val initialRoute = when {
            isFirstLaunch -> LocalAuthRoute()
            isUnlockingRequired -> UnlockRoute
            else -> HomeRoute
        }

        val backStack = rememberNavBackStack(appNavConfiguration, initialRoute)

        CompositionLocalProvider(
            LocalProvidableLocaleList provides localeList,
            LocalLayoutDirection provides layoutDirection
        ) {
            PassGenTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = modifier.secureContent().fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavDisplay(
                        backStack = backStack,
                        onBack = {
                            if (backStack.size > 1) {
                                backStack.removeLastOrNull()
                            }
                        },
                        entryProvider = { key ->
                            NavEntry(key) {
                                when (key) {
                                    is LocalAuthRoute -> NavEntryScopedViewModelStore(key) {
                                        SecretSetupScreen(
                                            onFinished = {
                                                setFirstLaunch(false)
                                                backStack.removeLastOrNull()
                                                if (backStack.isEmpty()) backStack.add(HomeRoute)
                                            }
                                        )
                                    }

                                    is AddConfigRoute -> NavEntryScopedViewModelStore(key) {
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

                                    is UnlockRoute -> UnlockingGateScreen(
                                        onUnlocked = {
                                            backStack.clear()
                                            backStack.add(HomeRoute)
                                        }
                                    )

                                    is HomeRoute,
                                    is RandomRoute,
                                    is SavedPasswordsRoute,
                                    is SettingsRoute,
                                    is AboutRoute -> MainAppContent(
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

                                    else -> Text("Unknown Route")
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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

    val localizedDrawerItems = listOf(
        NavItem(HomeRoute, stringResource(Res.string.nav_home), Icons.Default.Home),
        NavItem(RandomRoute, stringResource(Res.string.nav_random), Icons.Default.Shuffle),
        NavItem(
            SavedPasswordsRoute,
            stringResource(Res.string.nav_saved_passwords),
            Icons.Default.Lock
        ),
        NavItem(SettingsRoute, stringResource(Res.string.nav_settings), Icons.Default.Settings),
        NavItem(AboutRoute, stringResource(Res.string.nav_about), Icons.Default.Info),
    )

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(12.dp))

                localizedDrawerItems.forEach { item ->
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
                            text = localizedDrawerItems
                                .find { it.route == currentRoute }
                                ?.label
                                ?: stringResource(Res.string.app_name)
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
                                contentDescription = stringResource(Res.string.content_desc_menu)
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
