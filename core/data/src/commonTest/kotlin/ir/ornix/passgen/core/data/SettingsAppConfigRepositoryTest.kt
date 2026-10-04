package ir.ornix.passgen.core.data

import com.russhwolf.settings.MapSettings
import ir.ornix.passgen.core.model.AppLanguage
import ir.ornix.passgen.core.model.AppTheme
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsAppConfigRepositoryTest {

    @Test
    fun testFirstLaunchDefaultsAndPersistence() = runTest {
        val settings = MapSettings()
        val repository = SettingsAppConfigRepository(settings)

        assertTrue(repository.isFirstLaunch())

        repository.setFirstLaunch(false)
        assertFalse(repository.isFirstLaunch())

        val reloadedRepository = SettingsAppConfigRepository(settings)
        assertFalse(reloadedRepository.isFirstLaunch())
    }

    @Test
    fun testLanguageDefaultsAndPersistence() = runTest {
        val settings = MapSettings()
        val repository = SettingsAppConfigRepository(settings)

        assertEquals(AppLanguage.SYSTEM, repository.getLanguage().value)

        repository.setLanguage(AppLanguage.PERSIAN)
        assertEquals(AppLanguage.PERSIAN, repository.getLanguage().value)

        val reloadedRepository = SettingsAppConfigRepository(settings)
        assertEquals(AppLanguage.PERSIAN, reloadedRepository.getLanguage().value)

        repository.setLanguage(AppLanguage.ENGLISH)
        assertEquals(AppLanguage.ENGLISH, repository.getLanguage().value)

        repository.setLanguage(AppLanguage.SYSTEM)
        assertEquals(AppLanguage.SYSTEM, repository.getLanguage().value)
    }

    @Test
    fun testLanguageFallbackForUnknownCode() = runTest {
        val settings = MapSettings()
        settings.putString("app_language", "unknown_locale_xyz")

        val repository = SettingsAppConfigRepository(settings)
        assertEquals(AppLanguage.SYSTEM, repository.getLanguage().value)
    }

    @Test
    fun testThemeDefaultsAndPersistence() = runTest {
        val settings = MapSettings()
        val repository = SettingsAppConfigRepository(settings)

        assertEquals(AppTheme.SYSTEM, repository.getTheme().value)

        repository.setTheme(AppTheme.DARK)
        assertEquals(AppTheme.DARK, repository.getTheme().value)

        val reloadedRepository = SettingsAppConfigRepository(settings)
        assertEquals(AppTheme.DARK, reloadedRepository.getTheme().value)

        repository.setTheme(AppTheme.LIGHT)
        assertEquals(AppTheme.LIGHT, repository.getTheme().value)

        repository.setTheme(AppTheme.SYSTEM)
        assertEquals(AppTheme.SYSTEM, repository.getTheme().value)
    }
}
