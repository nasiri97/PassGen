package ir.ornix.passgen.feature.settings.impl.presentation

import ir.ornix.passgen.core.domain.AppConfigRepository
import ir.ornix.passgen.core.domain.BiometricAuthenticator
import ir.ornix.passgen.core.domain.Crypto
import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.domain.LocalAuthType
import ir.ornix.passgen.core.domain.appconfig.GetLanguageUseCase
import ir.ornix.passgen.core.domain.appconfig.GetThemeUseCase
import ir.ornix.passgen.core.domain.appconfig.GetVisibilityTimeoutUseCase
import ir.ornix.passgen.core.domain.appconfig.IsCopyEnabledUseCase
import ir.ornix.passgen.core.domain.appconfig.SetCopyEnabledUseCase
import ir.ornix.passgen.core.domain.appconfig.SetLanguageUseCase
import ir.ornix.passgen.core.domain.appconfig.SetThemeUseCase
import ir.ornix.passgen.core.domain.appconfig.SetVisibilityTimeoutUseCase
import ir.ornix.passgen.core.domain.localauth.GetLocalAuthTypeUseCase
import ir.ornix.passgen.core.domain.localauth.IsBiometricEnabledUseCase
import ir.ornix.passgen.core.domain.localauth.SaveLocalAuthSecretUseCase
import ir.ornix.passgen.core.domain.localauth.SetBiometricEnabledUseCase
import ir.ornix.passgen.core.model.AppLanguage
import ir.ornix.passgen.core.model.AppTheme
import ir.ornix.passgen.core.model.PasswordVisibilityTimeout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeBiometricAuthenticator(val available: Boolean = true) :
        BiometricAuthenticator {
        override val defaultDescription: String = "Test"
        override fun isBiometricAvailable(): Boolean = available
        override fun authenticate(
            title: String,
            subtitle: String,
            description: String,
            onSuccess: () -> Unit,
            onError: (String) -> Unit
        ) {
            if (available) onSuccess() else onError("Not available")
        }
    }

    private class FakeCrypto : Crypto {
        override fun encrypt(data: ByteArray): ByteArray = data
        override fun decrypt(data: ByteArray): ByteArray = data
    }

    private class FakeLocalAuthRepository : LocalAuthRepository {
        val authTypeFlow = MutableStateFlow(LocalAuthType.NONE)
        val biometricFlow = MutableStateFlow(false)

        override fun getLocalAuthType(): StateFlow<LocalAuthType> = authTypeFlow.asStateFlow()
        override fun setLocalAuthType(type: LocalAuthType) {
            authTypeFlow.value = type
        }

        override fun saveEncryptedSecret(encryptedSecret: ByteArray) {}
        override fun getEncryptedSecret(): ByteArray = ByteArray(0)
        override fun isBiometricEnabled(): StateFlow<Boolean> = biometricFlow.asStateFlow()
        override fun setBiometricEnabled(enabled: Boolean) {
            biometricFlow.value = enabled
        }

        override fun clearAuth() {
            authTypeFlow.value = LocalAuthType.NONE
            biometricFlow.value = false
        }
    }

    private class FakeAppConfigRepository : AppConfigRepository {
        val languageFlow = MutableStateFlow(AppLanguage.SYSTEM)
        val themeFlow = MutableStateFlow(AppTheme.SYSTEM)
        val copyEnabledFlow = MutableStateFlow(false)
        val visibilityTimeoutFlow = MutableStateFlow(PasswordVisibilityTimeout.default)

        override fun isFirstLaunch(): Boolean = false
        override fun setFirstLaunch(isFirstLaunch: Boolean) {}
        override fun getLanguage(): StateFlow<AppLanguage> = languageFlow.asStateFlow()
        override fun setLanguage(language: AppLanguage) {
            languageFlow.value = language
        }

        override fun getTheme(): StateFlow<AppTheme> = themeFlow.asStateFlow()
        override fun setTheme(theme: AppTheme) {
            themeFlow.value = theme
        }

        override fun isCopyEnabled(): StateFlow<Boolean> = copyEnabledFlow.asStateFlow()
        override fun setCopyEnabled(enabled: Boolean) {
            copyEnabledFlow.value = enabled
        }

        override fun getVisibilityTimeout(): StateFlow<PasswordVisibilityTimeout> =
            visibilityTimeoutFlow.asStateFlow()

        override fun setVisibilityTimeout(timeout: PasswordVisibilityTimeout) {
            visibilityTimeoutFlow.value = timeout
        }
    }

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testLanguageSelectionIntentUpdatesState() = runTest {
        val appConfigRepo = FakeAppConfigRepository()
        val localAuthRepo = FakeLocalAuthRepository()
        val biometricAuth = FakeBiometricAuthenticator()

        val viewModel = SettingsViewModel(
            isBiometricEnabled = IsBiometricEnabledUseCase(localAuthRepo),
            setBiometricEnabled = SetBiometricEnabledUseCase(localAuthRepo),
            biometricAuthenticator = biometricAuth,
            getLocalAuthType = GetLocalAuthTypeUseCase(localAuthRepo),
            saveLocalAuthSecret = SaveLocalAuthSecretUseCase(FakeCrypto(), localAuthRepo),
            getLanguage = GetLanguageUseCase(appConfigRepo),
            setLanguage = SetLanguageUseCase(appConfigRepo),
            getTheme = GetThemeUseCase(appConfigRepo),
            setTheme = SetThemeUseCase(appConfigRepo),
            isCopyEnabled = IsCopyEnabledUseCase(appConfigRepo),
            setCopyEnabled = SetCopyEnabledUseCase(appConfigRepo),
            getVisibilityTimeout = GetVisibilityTimeoutUseCase(appConfigRepo),
            setVisibilityTimeout = SetVisibilityTimeoutUseCase(appConfigRepo)
        )

        advanceUntilIdle()
        assertEquals(AppLanguage.SYSTEM, viewModel.uiState.value.selectedLanguage)

        viewModel.dispatch(SettingsIntent.SelectLanguage(AppLanguage.PERSIAN))
        advanceUntilIdle()
        assertEquals(AppLanguage.PERSIAN, viewModel.uiState.value.selectedLanguage)

        viewModel.dispatch(SettingsIntent.SelectLanguage(AppLanguage.ENGLISH))
        advanceUntilIdle()
        assertEquals(AppLanguage.ENGLISH, viewModel.uiState.value.selectedLanguage)
    }

    @Test
    fun testThemeSelectionIntentUpdatesState() = runTest {
        val appConfigRepo = FakeAppConfigRepository()
        val localAuthRepo = FakeLocalAuthRepository()
        val biometricAuth = FakeBiometricAuthenticator()

        val viewModel = SettingsViewModel(
            isBiometricEnabled = IsBiometricEnabledUseCase(localAuthRepo),
            setBiometricEnabled = SetBiometricEnabledUseCase(localAuthRepo),
            biometricAuthenticator = biometricAuth,
            getLocalAuthType = GetLocalAuthTypeUseCase(localAuthRepo),
            saveLocalAuthSecret = SaveLocalAuthSecretUseCase(FakeCrypto(), localAuthRepo),
            getLanguage = GetLanguageUseCase(appConfigRepo),
            setLanguage = SetLanguageUseCase(appConfigRepo),
            getTheme = GetThemeUseCase(appConfigRepo),
            setTheme = SetThemeUseCase(appConfigRepo),
            isCopyEnabled = IsCopyEnabledUseCase(appConfigRepo),
            setCopyEnabled = SetCopyEnabledUseCase(appConfigRepo),
            getVisibilityTimeout = GetVisibilityTimeoutUseCase(appConfigRepo),
            setVisibilityTimeout = SetVisibilityTimeoutUseCase(appConfigRepo)
        )

        advanceUntilIdle()
        assertEquals(AppTheme.SYSTEM, viewModel.uiState.value.selectedTheme)

        viewModel.dispatch(SettingsIntent.SelectTheme(AppTheme.DARK))
        advanceUntilIdle()
        assertEquals(AppTheme.DARK, viewModel.uiState.value.selectedTheme)

        viewModel.dispatch(SettingsIntent.SelectTheme(AppTheme.LIGHT))
        advanceUntilIdle()
        assertEquals(AppTheme.LIGHT, viewModel.uiState.value.selectedTheme)
    }

    @Test
    fun testCopyEnabledWarningDialogAndConfirmation() = runTest {
        val appConfigRepo = FakeAppConfigRepository()
        val localAuthRepo = FakeLocalAuthRepository()
        val biometricAuth = FakeBiometricAuthenticator()

        val viewModel = SettingsViewModel(
            isBiometricEnabled = IsBiometricEnabledUseCase(localAuthRepo),
            setBiometricEnabled = SetBiometricEnabledUseCase(localAuthRepo),
            biometricAuthenticator = biometricAuth,
            getLocalAuthType = GetLocalAuthTypeUseCase(localAuthRepo),
            saveLocalAuthSecret = SaveLocalAuthSecretUseCase(FakeCrypto(), localAuthRepo),
            getLanguage = GetLanguageUseCase(appConfigRepo),
            setLanguage = SetLanguageUseCase(appConfigRepo),
            getTheme = GetThemeUseCase(appConfigRepo),
            setTheme = SetThemeUseCase(appConfigRepo),
            isCopyEnabled = IsCopyEnabledUseCase(appConfigRepo),
            setCopyEnabled = SetCopyEnabledUseCase(appConfigRepo),
            getVisibilityTimeout = GetVisibilityTimeoutUseCase(appConfigRepo),
            setVisibilityTimeout = SetVisibilityTimeoutUseCase(appConfigRepo)
        )

        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isCopyEnabled)

        // Request enabling copy triggers warning dialog
        viewModel.dispatch(SettingsIntent.RequestSetCopyEnabled(true))
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.showCopyWarningDialog)

        // Confirm enabling copy closes dialog and enables copy
        viewModel.dispatch(SettingsIntent.ConfirmEnableCopy)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.showCopyWarningDialog)
        assertTrue(viewModel.uiState.value.isCopyEnabled)
    }

    @Test
    fun testVisibilityTimeoutSelection() = runTest {
        val appConfigRepo = FakeAppConfigRepository()
        val localAuthRepo = FakeLocalAuthRepository()
        val biometricAuth = FakeBiometricAuthenticator()

        val viewModel = SettingsViewModel(
            isBiometricEnabled = IsBiometricEnabledUseCase(localAuthRepo),
            setBiometricEnabled = SetBiometricEnabledUseCase(localAuthRepo),
            biometricAuthenticator = biometricAuth,
            getLocalAuthType = GetLocalAuthTypeUseCase(localAuthRepo),
            saveLocalAuthSecret = SaveLocalAuthSecretUseCase(FakeCrypto(), localAuthRepo),
            getLanguage = GetLanguageUseCase(appConfigRepo),
            setLanguage = SetLanguageUseCase(appConfigRepo),
            getTheme = GetThemeUseCase(appConfigRepo),
            setTheme = SetThemeUseCase(appConfigRepo),
            isCopyEnabled = IsCopyEnabledUseCase(appConfigRepo),
            setCopyEnabled = SetCopyEnabledUseCase(appConfigRepo),
            getVisibilityTimeout = GetVisibilityTimeoutUseCase(appConfigRepo),
            setVisibilityTimeout = SetVisibilityTimeoutUseCase(appConfigRepo)
        )

        advanceUntilIdle()
        assertEquals(
            PasswordVisibilityTimeout.SECONDS_20,
            viewModel.uiState.value.selectedVisibilityTimeout
        )

        viewModel.dispatch(SettingsIntent.SelectVisibilityTimeout(PasswordVisibilityTimeout.SECONDS_30))
        advanceUntilIdle()
        assertEquals(
            PasswordVisibilityTimeout.SECONDS_30,
            viewModel.uiState.value.selectedVisibilityTimeout
        )
    }
}
