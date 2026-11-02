package ir.ornix.passgen.feature.settings.impl.presentation

import ir.ornix.passgen.core.domain.AppConfigRepository
import ir.ornix.passgen.core.domain.AutofillService
import ir.ornix.passgen.core.domain.BiometricAuthenticator
import ir.ornix.passgen.core.domain.CredentialRepository
import ir.ornix.passgen.core.domain.Crypto
import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.domain.PassGenConfigRepository
import ir.ornix.passgen.core.domain.appconfig.GetLanguageUseCase
import ir.ornix.passgen.core.domain.appconfig.GetThemeUseCase
import ir.ornix.passgen.core.domain.appconfig.GetVisibilityTimeoutUseCase
import ir.ornix.passgen.core.domain.appconfig.IsCopyEnabledUseCase
import ir.ornix.passgen.core.domain.appconfig.SetCopyEnabledUseCase
import ir.ornix.passgen.core.domain.appconfig.SetLanguageUseCase
import ir.ornix.passgen.core.domain.appconfig.SetThemeUseCase
import ir.ornix.passgen.core.domain.appconfig.SetVisibilityTimeoutUseCase
import ir.ornix.passgen.core.domain.data_management.PurgeSensitiveDataUseCase
import ir.ornix.passgen.core.domain.localauth.GetFailedAuthAttemptLimitUseCase
import ir.ornix.passgen.core.domain.localauth.GetLocalAuthTypeUseCase
import ir.ornix.passgen.core.domain.localauth.IsBiometricEnabledUseCase
import ir.ornix.passgen.core.domain.localauth.SaveLocalAuthSecretUseCase
import ir.ornix.passgen.core.domain.localauth.SetBiometricEnabledUseCase
import ir.ornix.passgen.core.domain.localauth.UpdateFailedAuthAttemptLimitUseCase
import ir.ornix.passgen.core.model.AppLanguage
import ir.ornix.passgen.core.model.AppTheme
import ir.ornix.passgen.core.model.Credential
import ir.ornix.passgen.core.model.PasswordVisibilityTimeout
import ir.ornix.passgen.core.model.localauth.FailedAuthAttemptLimit
import ir.ornix.passgen.core.model.localauth.LocalAuthType
import ir.ornix.passgen.core.model.passgenconfig.KdfPassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.PassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.RandomPassGenConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
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

    private class FakeAutofillService(
        var supported: Boolean = true,
        var enabled: Boolean = false
    ) : AutofillService {
        var enableRequested: Boolean = false
        override fun isSupported(): Boolean = supported
        override fun isAutofillEnabled(): Boolean = enabled
        override fun requestEnableAutofill() {
            enableRequested = true
        }
    }

    private class FakeLocalAuthRepository : LocalAuthRepository {
        val authTypeFlow = MutableStateFlow(LocalAuthType.NONE)
        val biometricFlow = MutableStateFlow(false)
        val failedAttemptCountFlow = MutableStateFlow(0L)
        val failedAttemptLimitFlow = MutableStateFlow(FailedAuthAttemptLimit.DISABLED.maxAttempts)

        override fun getLocalAuthType(): StateFlow<LocalAuthType> = authTypeFlow.asStateFlow()
        override suspend fun setLocalAuthType(type: LocalAuthType) {
            authTypeFlow.value = type
        }

        override suspend fun saveEncryptedSecret(encryptedSecret: ByteArray) {}
        override suspend fun getEncryptedSecret(): ByteArray = ByteArray(0)

        override fun isBiometricEnabled(): StateFlow<Boolean> = biometricFlow.asStateFlow()
        override suspend fun setBiometricEnabled(enabled: Boolean) {
            biometricFlow.value = enabled
        }

        override fun getFailedAuthAttemptCount(): StateFlow<Long> =
            failedAttemptCountFlow.asStateFlow()

        override suspend fun incrementFailedAuthAttemptCount() {
            failedAttemptCountFlow.value++
        }

        override suspend fun resetFailedAuthAttemptCount() {
            failedAttemptCountFlow.value = 0L
        }

        override fun getFailedAuthAttemptLimit(): StateFlow<Long> =
            failedAttemptLimitFlow.asStateFlow()

        override suspend fun updateFailedAuthAttemptLimit(maxAttempts: Long) {
            failedAttemptLimitFlow.value = maxAttempts
        }

        override suspend fun disableFailedAuthAttemptLimit() {
            failedAttemptLimitFlow.value = FailedAuthAttemptLimit.DISABLED.maxAttempts
        }

        override suspend fun clearAuth() {
            authTypeFlow.value = LocalAuthType.NONE
            biometricFlow.value = false
            failedAttemptCountFlow.value = 0L
            failedAttemptLimitFlow.value = FailedAuthAttemptLimit.DISABLED.maxAttempts
        }
    }

    private class FakeAppConfigRepository : AppConfigRepository {
        val languageFlow = MutableStateFlow(AppLanguage.SYSTEM)
        val themeFlow = MutableStateFlow(AppTheme.SYSTEM)
        val copyEnabledFlow = MutableStateFlow(false)
        val visibilityTimeoutFlow = MutableStateFlow(PasswordVisibilityTimeout.default)

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

    private class FakeCredentialRepository : CredentialRepository {
        override suspend fun save(credential: Credential) {}
        override suspend fun delete(credentialId: Long) {}
        override fun getAll(): Flow<List<Credential>> =
            MutableStateFlow<List<Credential>>(emptyList()).asStateFlow()

        override suspend fun deleteAllCredentials() {}
    }

    private class FakeHmacSigner : HmacSigner {
        override suspend fun registerKey(mkdId: String, rawMasterKey: ByteArray) {}
        override suspend fun sign(mkdId: String, input: String): ByteArray = ByteArray(0)
        override suspend fun hasMasterKeyDigest(mkdId: String): Boolean = false
        override suspend fun deleteMasterKeyDigest(mkdId: String) {}
    }

    private class FakePassGenConfigRepository : PassGenConfigRepository {
        override suspend fun add(config: PassGenConfig): Int = 0
        override suspend fun removeKdfPassGenConfigById(configId: Int) {}
        override suspend fun removeRandomPassGenConfigById(configId: Int) {}
        override suspend fun getAllKdfPassGenConfigIds(): List<Int> = emptyList()
        override fun getAllKdfPassGenConfigs(): Flow<List<KdfPassGenConfig>> =
            MutableStateFlow<List<KdfPassGenConfig>>(emptyList()).asStateFlow()

        override fun getAllRandomPassGenConfigs(): Flow<List<RandomPassGenConfig>> =
            MutableStateFlow<List<RandomPassGenConfig>>(emptyList()).asStateFlow()

        override suspend fun deleteAllPassGenConfigs() {}
    }

    private fun createViewModel(
        appConfigRepo: AppConfigRepository = FakeAppConfigRepository(),
        localAuthRepo: FakeLocalAuthRepository = FakeLocalAuthRepository(),
        biometricAuth: BiometricAuthenticator = FakeBiometricAuthenticator(),
        autofillService: AutofillService = FakeAutofillService()
    ): SettingsViewModel {
        val purgeSensitiveDataUseCase = PurgeSensitiveDataUseCase(
            hmacSigner = FakeHmacSigner(),
            passGenConfigRepository = FakePassGenConfigRepository(),
            credentialRepository = FakeCredentialRepository(),
            localAuthRepository = localAuthRepo
        )

        return SettingsViewModel(
            isBiometricEnabled = IsBiometricEnabledUseCase(localAuthRepo),
            setBiometricEnabled = SetBiometricEnabledUseCase(localAuthRepo),
            biometricAuthenticator = biometricAuth,
            getLocalAuthType = GetLocalAuthTypeUseCase(localAuthRepo),
            saveLocalAuthSecret = SaveLocalAuthSecretUseCase(FakeCrypto(), localAuthRepo),
            getFailedAuthAttemptLimit = GetFailedAuthAttemptLimitUseCase(localAuthRepo),
            updateFailedAuthAttemptLimit = UpdateFailedAuthAttemptLimitUseCase(localAuthRepo),
            getLanguage = GetLanguageUseCase(appConfigRepo),
            setLanguage = SetLanguageUseCase(appConfigRepo),
            getTheme = GetThemeUseCase(appConfigRepo),
            setTheme = SetThemeUseCase(appConfigRepo),
            isCopyEnabled = IsCopyEnabledUseCase(appConfigRepo),
            setCopyEnabled = SetCopyEnabledUseCase(appConfigRepo),
            getVisibilityTimeout = GetVisibilityTimeoutUseCase(appConfigRepo),
            setVisibilityTimeout = SetVisibilityTimeoutUseCase(appConfigRepo),
            autofillService = autofillService,
            purgeSensitiveData = purgeSensitiveDataUseCase
        )
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
        val viewModel = createViewModel()

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
        val viewModel = createViewModel()

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
    fun testFailedAuthAttemptLimitSelectionAndObservation() = runTest {
        val localAuthRepo = FakeLocalAuthRepository()
        val viewModel = createViewModel(localAuthRepo = localAuthRepo)

        advanceUntilIdle()
        assertEquals(
            FailedAuthAttemptLimit.DISABLED,
            viewModel.uiState.value.selectedFailedAuthAttemptLimit
        )

        viewModel.dispatch(SettingsIntent.SelectFailedAuthAttemptLimit(FailedAuthAttemptLimit.THREE_ATTEMPTS))
        advanceUntilIdle()
        assertEquals(
            FailedAuthAttemptLimit.THREE_ATTEMPTS,
            viewModel.uiState.value.selectedFailedAuthAttemptLimit
        )
        assertEquals(3L, localAuthRepo.failedAttemptLimitFlow.value)

        viewModel.dispatch(SettingsIntent.SelectFailedAuthAttemptLimit(FailedAuthAttemptLimit.FIVE_ATTEMPTS))
        advanceUntilIdle()
        assertEquals(
            FailedAuthAttemptLimit.FIVE_ATTEMPTS,
            viewModel.uiState.value.selectedFailedAuthAttemptLimit
        )
        assertEquals(5L, localAuthRepo.failedAttemptLimitFlow.value)

        viewModel.dispatch(SettingsIntent.SelectFailedAuthAttemptLimit(FailedAuthAttemptLimit.ONE_ATTEMPT))
        advanceUntilIdle()
        assertEquals(
            FailedAuthAttemptLimit.ONE_ATTEMPT,
            viewModel.uiState.value.selectedFailedAuthAttemptLimit
        )
        assertEquals(1L, localAuthRepo.failedAttemptLimitFlow.value)

        viewModel.dispatch(SettingsIntent.SelectFailedAuthAttemptLimit(FailedAuthAttemptLimit.DISABLED))
        advanceUntilIdle()
        assertEquals(
            FailedAuthAttemptLimit.DISABLED,
            viewModel.uiState.value.selectedFailedAuthAttemptLimit
        )
        assertEquals(Long.MAX_VALUE, localAuthRepo.failedAttemptLimitFlow.value)
    }

    @Test
    fun testSetFingerprintEnabled() = runTest {
        val localAuthRepo = FakeLocalAuthRepository()
        val viewModel = createViewModel(localAuthRepo = localAuthRepo)

        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isBiometricEnabled)

        viewModel.dispatch(SettingsIntent.SetFingerprintEnabled(true))
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isBiometricEnabled)
        assertTrue(localAuthRepo.biometricFlow.value)

        viewModel.dispatch(SettingsIntent.SetFingerprintEnabled(false))
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isBiometricEnabled)
        assertFalse(localAuthRepo.biometricFlow.value)
    }

    @Test
    fun testBiometricAvailabilityStatus() = runTest {
        val unavailableBiometricAuth = FakeBiometricAuthenticator(available = false)
        val viewModelUnavailable = createViewModel(biometricAuth = unavailableBiometricAuth)

        advanceUntilIdle()
        assertFalse(viewModelUnavailable.uiState.value.isBiometricAvailable)

        val availableBiometricAuth = FakeBiometricAuthenticator(available = true)
        val viewModelAvailable = createViewModel(biometricAuth = availableBiometricAuth)

        advanceUntilIdle()
        assertTrue(viewModelAvailable.uiState.value.isBiometricAvailable)
    }

    @Test
    fun testLocalAuthenticationTypeObservationAndDisable() = runTest {
        val localAuthRepo = FakeLocalAuthRepository()
        localAuthRepo.authTypeFlow.value = LocalAuthType.PIN
        val viewModel = createViewModel(localAuthRepo = localAuthRepo)

        advanceUntilIdle()
        assertEquals(LocalAuthType.PIN, viewModel.uiState.value.currentLocalAuthType)

        viewModel.dispatch(SettingsIntent.LocalAuthenticationDisabled)
        advanceUntilIdle()
        assertEquals(LocalAuthType.NONE, viewModel.uiState.value.currentLocalAuthType)
        assertEquals(LocalAuthType.NONE, localAuthRepo.authTypeFlow.value)
    }

    @Test
    fun testCopyEnabledWarningDialogAndConfirmation() = runTest {
        val viewModel = createViewModel()

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

        // Disabling copy directly without dialog
        viewModel.dispatch(SettingsIntent.RequestSetCopyEnabled(false))
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.showCopyWarningDialog)
        assertFalse(viewModel.uiState.value.isCopyEnabled)
    }

    @Test
    fun testDismissCopyWarningDialog() = runTest {
        val viewModel = createViewModel()

        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isCopyEnabled)

        viewModel.dispatch(SettingsIntent.RequestSetCopyEnabled(true))
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.showCopyWarningDialog)

        viewModel.dispatch(SettingsIntent.DismissCopyWarningDialog)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.showCopyWarningDialog)
        assertFalse(viewModel.uiState.value.isCopyEnabled)
    }

    @Test
    fun testPurgeSensitiveDataWarningDialogAndConfirmation() = runTest {
        val viewModel = createViewModel()

        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.showPurgeSensitiveDataWarningDialog)

        viewModel.dispatch(SettingsIntent.RequestPurgeSensitiveData)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.showPurgeSensitiveDataWarningDialog)

        viewModel.dispatch(SettingsIntent.ConfirmPurgeSensitiveData)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.showPurgeSensitiveDataWarningDialog)
    }

    @Test
    fun testDismissPurgeSensitiveDataWarningDialog() = runTest {
        val viewModel = createViewModel()

        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.showPurgeSensitiveDataWarningDialog)

        viewModel.dispatch(SettingsIntent.RequestPurgeSensitiveData)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.showPurgeSensitiveDataWarningDialog)

        viewModel.dispatch(SettingsIntent.DismissPurgeSensitiveDataWarning)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.showPurgeSensitiveDataWarningDialog)
    }

    @Test
    fun testVisibilityTimeoutSelection() = runTest {
        val viewModel = createViewModel()

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

    @Test
    fun testAutofillBannerVisibilityAndRequestEnable() = runTest {
        val autofillService = FakeAutofillService(supported = true, enabled = false)
        val viewModel = createViewModel(autofillService = autofillService)

        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.showAutofillBanner)

        viewModel.dispatch(SettingsIntent.RequestEnableAutofill)
        advanceUntilIdle()
        assertTrue(autofillService.enableRequested)

        autofillService.enabled = true
        viewModel.dispatch(SettingsIntent.CheckAutofillStatus)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.showAutofillBanner)
    }
}
