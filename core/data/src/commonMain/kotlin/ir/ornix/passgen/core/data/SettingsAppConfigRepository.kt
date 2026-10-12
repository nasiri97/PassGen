package ir.ornix.passgen.core.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import ir.ornix.passgen.core.domain.AppConfigRepository
import ir.ornix.passgen.core.model.AppLanguage
import ir.ornix.passgen.core.model.AppTheme
import ir.ornix.passgen.core.model.PasswordVisibilityTimeout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsAppConfigRepository(private val settings: Settings = Settings()) :
    AppConfigRepository {

    companion object {
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_LANGUAGE = "app_language"
        private const val KEY_THEME = "app_theme"
        private const val KEY_COPY_ENABLED = "copy_enabled"
        private const val KEY_VISIBILITY_TIMEOUT = "visibility_timeout"

        private const val DEFAULT_IS_FIRST_LAUNCH = true
        private const val DEFAULT_IS_COPY_ENABLED = false
        private val DEFAULT_VISIBILITY_TIMEOUT = PasswordVisibilityTimeout.default
    }

    private val languageFlow = MutableStateFlow(
        AppLanguage.fromCode(settings.getStringOrNull(KEY_LANGUAGE))
    )

    private val themeFlow = MutableStateFlow(
        AppTheme.fromKey(settings.getStringOrNull(KEY_THEME))
    )

    private val copyEnabledFlow = MutableStateFlow(
        settings.getBoolean(KEY_COPY_ENABLED, DEFAULT_IS_COPY_ENABLED)
    )

    private val visibilityTimeoutFlow = MutableStateFlow(
        PasswordVisibilityTimeout.fromKey(
            settings.getString(KEY_VISIBILITY_TIMEOUT, DEFAULT_VISIBILITY_TIMEOUT.storageKey)
        )
    )

    override fun isFirstLaunch(): Boolean {
        return settings.getBoolean(KEY_FIRST_LAUNCH, DEFAULT_IS_FIRST_LAUNCH)
    }

    override fun setFirstLaunch(isFirstLaunch: Boolean) {
        settings[KEY_FIRST_LAUNCH] = isFirstLaunch
    }

    override fun getLanguage(): StateFlow<AppLanguage> = languageFlow.asStateFlow()

    override fun setLanguage(language: AppLanguage) {
        settings[KEY_LANGUAGE] = language.code
        languageFlow.value = language
    }

    override fun getTheme(): StateFlow<AppTheme> = themeFlow.asStateFlow()

    override fun setTheme(theme: AppTheme) {
        settings[KEY_THEME] = theme.storageKey
        themeFlow.value = theme
    }

    override fun isCopyEnabled(): StateFlow<Boolean> = copyEnabledFlow.asStateFlow()

    override fun setCopyEnabled(enabled: Boolean) {
        settings[KEY_COPY_ENABLED] = enabled
        copyEnabledFlow.value = enabled
    }

    override fun getVisibilityTimeout(): StateFlow<PasswordVisibilityTimeout> =
        visibilityTimeoutFlow.asStateFlow()

    override fun setVisibilityTimeout(timeout: PasswordVisibilityTimeout) {
        settings[KEY_VISIBILITY_TIMEOUT] = timeout.storageKey
        visibilityTimeoutFlow.value = timeout
    }
}
