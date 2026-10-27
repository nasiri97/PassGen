package ir.ornix.passgen.core.ui.util

import ir.ornix.passgen.core.model.AppLanguage

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("() => { try { delete navigator.language; delete navigator.languages; if (document && document.documentElement) { document.documentElement.removeAttribute('lang'); document.documentElement.removeAttribute('dir'); } } catch(e) { console.error(e); } }")
private external fun resetWebLanguage()

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(code) => { try { Object.defineProperty(navigator, 'language', { get: function() { return code; }, configurable: true }); Object.defineProperty(navigator, 'languages', { get: function() { return [code]; }, configurable: true }); if (document && document.documentElement) { document.documentElement.lang = code; } } catch(e) { console.error(e); } }")
private external fun setWebLanguage(code: String)

actual fun setAppLocale(language: AppLanguage) {
    when (language) {
        AppLanguage.SYSTEM -> resetWebLanguage()
        AppLanguage.ENGLISH -> setWebLanguage("en")
        AppLanguage.PERSIAN -> setWebLanguage("fa")
    }
}
