package ir.ornix.passgen.core.ui.util

import ir.ornix.passgen.core.model.AppLanguage
import java.util.Locale

private val initialSystemLocale: Locale by lazy { Locale.getDefault() }

actual fun setAppLocale(language: AppLanguage) {
    val targetLocale = when (language) {
        AppLanguage.SYSTEM -> initialSystemLocale
        AppLanguage.ENGLISH -> Locale.ENGLISH
        AppLanguage.PERSIAN -> Locale.forLanguageTag("fa")
    }
    Locale.setDefault(targetLocale)
}
