package ir.ornix.passgen.core.data.repository

import ir.ornix.passgen.core.database.AppConfigEntity
import ir.ornix.passgen.core.database.PassGenDatabase
import ir.ornix.passgen.core.domain.AppConfigRepository
import ir.ornix.passgen.core.model.AppLanguage
import ir.ornix.passgen.core.model.AppTheme
import ir.ornix.passgen.core.model.PasswordVisibilityTimeout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SqlDelightAppConfigRepository(
    database: PassGenDatabase
) : AppConfigRepository {

    private val queries = database.appConfigQueries

    private val initialConfig: AppConfigEntity = queries.selectAppConfig().executeAsOneOrNull()
        ?: AppConfigEntity(
            id = 1,
            is_first_launch = 1,
            language = "en",
            theme = "system",
            is_copy_enabled = 0,
            visibility_timeout = PasswordVisibilityTimeout.default.storageKey
        )

    private val languageFlow = MutableStateFlow(AppLanguage.fromCode(initialConfig.language))
    private val themeFlow = MutableStateFlow(AppTheme.fromKey(initialConfig.theme))
    private val copyEnabledFlow = MutableStateFlow(initialConfig.is_copy_enabled == 1L)
    private val visibilityTimeoutFlow = MutableStateFlow(
        PasswordVisibilityTimeout.fromKey(initialConfig.visibility_timeout)
    )

    override fun isFirstLaunch(): Boolean {
        return (queries.selectAppConfig().executeAsOneOrNull()?.is_first_launch ?: 1L) == 1L
    }

    override fun setFirstLaunch(isFirstLaunch: Boolean) {
        queries.updateFirstLaunch(if (isFirstLaunch) 1L else 0L)
    }

    override fun getLanguage(): StateFlow<AppLanguage> = languageFlow.asStateFlow()

    override fun setLanguage(language: AppLanguage) {
        queries.updateLanguage(language.code)
        languageFlow.value = language
    }

    override fun getTheme(): StateFlow<AppTheme> = themeFlow.asStateFlow()

    override fun setTheme(theme: AppTheme) {
        queries.updateTheme(theme.storageKey)
        themeFlow.value = theme
    }

    override fun isCopyEnabled(): StateFlow<Boolean> = copyEnabledFlow.asStateFlow()

    override fun setCopyEnabled(enabled: Boolean) {
        queries.updateCopyEnabled(if (enabled) 1L else 0L)
        copyEnabledFlow.value = enabled
    }

    override fun getVisibilityTimeout(): StateFlow<PasswordVisibilityTimeout> =
        visibilityTimeoutFlow.asStateFlow()

    override fun setVisibilityTimeout(timeout: PasswordVisibilityTimeout) {
        queries.updateVisibilityTimeout(timeout.storageKey)
        visibilityTimeoutFlow.value = timeout
    }
}
