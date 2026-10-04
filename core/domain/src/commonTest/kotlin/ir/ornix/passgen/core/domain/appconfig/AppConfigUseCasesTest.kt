package ir.ornix.passgen.core.domain.appconfig

import ir.ornix.passgen.core.domain.AppConfigRepository
import ir.ornix.passgen.core.model.AppLanguage
import ir.ornix.passgen.core.model.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AppConfigUseCasesTest {

    private class FakeAppConfigRepository : AppConfigRepository {
        private var firstLaunch = true
        private val languageState = MutableStateFlow(AppLanguage.SYSTEM)
        private val themeState = MutableStateFlow(AppTheme.SYSTEM)

        override fun isFirstLaunch(): Boolean = firstLaunch

        override fun setFirstLaunch(isFirstLaunch: Boolean) {
            firstLaunch = isFirstLaunch
        }

        override fun getLanguage(): StateFlow<AppLanguage> = languageState.asStateFlow()

        override fun setLanguage(language: AppLanguage) {
            languageState.value = language
        }

        override fun getTheme(): StateFlow<AppTheme> = themeState.asStateFlow()

        override fun setTheme(theme: AppTheme) {
            themeState.value = theme
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
}
