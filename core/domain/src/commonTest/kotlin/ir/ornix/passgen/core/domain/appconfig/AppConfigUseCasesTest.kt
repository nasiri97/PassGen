package ir.ornix.passgen.core.domain.appconfig

import ir.ornix.passgen.core.domain.AppConfigRepository
import ir.ornix.passgen.core.model.AppLanguage
import ir.ornix.passgen.core.model.AppTheme
import ir.ornix.passgen.core.model.PasswordVisibilityTimeout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AppConfigUseCasesTest {

    private class FakeAppConfigRepository : AppConfigRepository {
        private val languageState = MutableStateFlow(AppLanguage.SYSTEM)
        private val themeState = MutableStateFlow(AppTheme.SYSTEM)
        private val copyEnabledState = MutableStateFlow(false)
        private val passwordVisibilityTimeoutState =
            MutableStateFlow(PasswordVisibilityTimeout.default)

        override fun getLanguage(): StateFlow<AppLanguage> = languageState.asStateFlow()

        override fun setLanguage(language: AppLanguage) {
            languageState.value = language
        }

        override fun getTheme(): StateFlow<AppTheme> = themeState.asStateFlow()

        override fun setTheme(theme: AppTheme) {
            themeState.value = theme
        }

        override fun isCopyEnabled(): StateFlow<Boolean> = copyEnabledState.asStateFlow()
        override fun setCopyEnabled(enabled: Boolean) {
            copyEnabledState.value = enabled
        }

        override fun getVisibilityTimeout(): StateFlow<PasswordVisibilityTimeout> =
            passwordVisibilityTimeoutState.asStateFlow()

        override fun setVisibilityTimeout(timeout: PasswordVisibilityTimeout) {
            passwordVisibilityTimeoutState.value = timeout
        }
    }

    @Test
    fun testLanguageUseCases() = runTest {
        val repository = FakeAppConfigRepository()
        val getLanguageUseCase = GetLanguageUseCase(repository)
        val setLanguageUseCase = SetLanguageUseCase(repository)

        assertEquals(AppLanguage.SYSTEM, getLanguageUseCase().value)

        setLanguageUseCase(AppLanguage.PERSIAN)
        assertEquals(AppLanguage.PERSIAN, getLanguageUseCase().value)

        setLanguageUseCase(AppLanguage.ENGLISH)
        assertEquals(AppLanguage.ENGLISH, getLanguageUseCase().value)
    }

    @Test
    fun testThemeUseCases() = runTest {
        val repository = FakeAppConfigRepository()
        val getThemeUseCase = GetThemeUseCase(repository)
        val setThemeUseCase = SetThemeUseCase(repository)

        assertEquals(AppTheme.SYSTEM, getThemeUseCase().value)

        setThemeUseCase(AppTheme.DARK)
        assertEquals(AppTheme.DARK, getThemeUseCase().value)

        setThemeUseCase(AppTheme.LIGHT)
        assertEquals(AppTheme.LIGHT, getThemeUseCase().value)
    }

    @Test
    fun testCopyAndTimeoutUseCases() = runTest {
        val repository = FakeAppConfigRepository()
        val isCopyEnabled = IsCopyEnabledUseCase(repository)
        val setCopyEnabled = SetCopyEnabledUseCase(repository)
        val getVisibilityTimeout = GetVisibilityTimeoutUseCase(repository)
        val setVisibilityTimeout = SetVisibilityTimeoutUseCase(repository)

        assertEquals(false, isCopyEnabled().value)
        setCopyEnabled(true)
        assertEquals(true, isCopyEnabled().value)

        assertEquals(PasswordVisibilityTimeout.SECONDS_20, getVisibilityTimeout().value)
        setVisibilityTimeout(PasswordVisibilityTimeout.SECONDS_30)
        assertEquals(PasswordVisibilityTimeout.SECONDS_30, getVisibilityTimeout().value)
    }
}
