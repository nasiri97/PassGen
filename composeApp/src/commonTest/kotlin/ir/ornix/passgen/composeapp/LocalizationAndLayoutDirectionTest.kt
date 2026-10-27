package ir.ornix.passgen.composeapp

import androidx.compose.ui.unit.LayoutDirection
import ir.ornix.passgen.core.model.AppLanguage
import ir.ornix.passgen.core.model.AppTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalizationAndLayoutDirectionTest {

    private fun resolveActiveLanguageCode(
        selectedLanguage: AppLanguage,
        systemLanguageCode: String
    ): String {
        return when (selectedLanguage) {
            AppLanguage.SYSTEM -> systemLanguageCode.lowercase()
            AppLanguage.ENGLISH -> "en"
            AppLanguage.PERSIAN -> "fa"
        }
    }

    private fun resolveLayoutDirection(activeLanguageCode: String): LayoutDirection {
        return if (activeLanguageCode in listOf("fa", "ar", "he", "ur")) {
            LayoutDirection.Rtl
        } else {
            LayoutDirection.Ltr
        }
    }

    @Test
    fun testPersianResolvesToRtl() {
        val activeCode = resolveActiveLanguageCode(AppLanguage.PERSIAN, "en")
        assertEquals("fa", activeCode)

        val direction = resolveLayoutDirection(activeCode)
        assertEquals(LayoutDirection.Rtl, direction)
    }

    @Test
    fun testEnglishResolvesToLtr() {
        val activeCode = resolveActiveLanguageCode(AppLanguage.ENGLISH, "fa")
        assertEquals("en", activeCode)

        val direction = resolveLayoutDirection(activeCode)
        assertEquals(LayoutDirection.Ltr, direction)
    }

    @Test
    fun testSystemDefaultFollowsSystemLanguage() {
        val activeCodeFa = resolveActiveLanguageCode(AppLanguage.SYSTEM, "fa")
        assertEquals("fa", activeCodeFa)
        assertEquals(LayoutDirection.Rtl, resolveLayoutDirection(activeCodeFa))

        val activeCodeEn = resolveActiveLanguageCode(AppLanguage.SYSTEM, "en")
        assertEquals("en", activeCodeEn)
        assertEquals(LayoutDirection.Ltr, resolveLayoutDirection(activeCodeEn))
    }

    @Test
    fun testUnsupportedSystemLanguageFallbackToLtr() {
        val activeCodeFr = resolveActiveLanguageCode(AppLanguage.SYSTEM, "fr")
        assertEquals("fr", activeCodeFr)
        assertEquals(LayoutDirection.Ltr, resolveLayoutDirection(activeCodeFr))
    }

    @Test
    fun testAppThemeEnumParsing() {
        assertEquals(AppTheme.SYSTEM, AppTheme.fromKey("system"))
        assertEquals(AppTheme.LIGHT, AppTheme.fromKey("light"))
        assertEquals(AppTheme.DARK, AppTheme.fromKey("dark"))
        assertEquals(AppTheme.SYSTEM, AppTheme.fromKey("invalid_theme"))
    }

    @Test
    fun testAppLanguageEnumParsing() {
        assertEquals(AppLanguage.SYSTEM, AppLanguage.fromCode("system"))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("en"))
        assertEquals(AppLanguage.PERSIAN, AppLanguage.fromCode("fa"))
        assertEquals(AppLanguage.SYSTEM, AppLanguage.fromCode("invalid_code"))
    }
}
