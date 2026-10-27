package ir.ornix.passgen.core.ui.util

import ir.ornix.passgen.core.model.AppLanguage
import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual fun setAppLocale(language: AppLanguage) {
    val code = when (language) {
        AppLanguage.SYSTEM -> NSLocale.currentLocale.languageCode ?: "en"
        AppLanguage.ENGLISH -> "en"
        AppLanguage.PERSIAN -> "fa"
    }
    NSUserDefaults.standardUserDefaults.setObject(listOf(code), "AppleLanguages")
}
