package ir.ornix.passgen.core.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.db.SqlDriver
import ir.ornix.passgen.core.database.AppConfigEntity
import ir.ornix.passgen.core.database.PassGenDatabase
import ir.ornix.passgen.core.domain.AppConfigRepository
import ir.ornix.passgen.core.model.AppLanguage
import ir.ornix.passgen.core.model.AppTheme
import ir.ornix.passgen.core.model.PasswordVisibilityTimeout
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SqlDelightAppConfigRepository(
    database: PassGenDatabase,
    private val driver: SqlDriver,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) : AppConfigRepository {

    private val queries = database.appConfigQueries
    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)

    private val defaultConfig = AppConfigEntity(
        id = 1,
        is_first_launch = 1,
        language = "en",
        theme = "system",
        is_copy_enabled = 0,
        visibility_timeout = PasswordVisibilityTimeout.default.storageKey
    )

    private val languageFlow = MutableStateFlow(AppLanguage.fromCode(defaultConfig.language))
    private val themeFlow = MutableStateFlow(AppTheme.fromKey(defaultConfig.theme))
    private val copyEnabledFlow = MutableStateFlow(defaultConfig.is_copy_enabled == 1L)
    private val visibilityTimeoutFlow =
        MutableStateFlow(PasswordVisibilityTimeout.fromKey(defaultConfig.visibility_timeout))
    private var firstLaunchVal = defaultConfig.is_first_launch == 1L

    init {
        scope.launch {
            try {
                PassGenDatabase.Schema.create(driver).await()
            } catch (_: Exception) {
            }

            try {
                val config = queries.selectAppConfig().awaitAsOneOrNull()
                if (config != null) {
                    languageFlow.value = AppLanguage.fromCode(config.language)
                    themeFlow.value = AppTheme.fromKey(config.theme)
                    copyEnabledFlow.value = config.is_copy_enabled == 1L
                    visibilityTimeoutFlow.value =
                        PasswordVisibilityTimeout.fromKey(config.visibility_timeout)
                    firstLaunchVal = config.is_first_launch == 1L
                }
            } catch (_: Exception) {
            }
        }
    }

    override fun isFirstLaunch(): Boolean = firstLaunchVal

    override fun setFirstLaunch(isFirstLaunch: Boolean) {
        firstLaunchVal = isFirstLaunch
        scope.launch {
            try {
                PassGenDatabase.Schema.create(driver).await()
                queries.updateFirstLaunch(if (isFirstLaunch) 1L else 0L)
            } catch (_: Exception) {
            }
        }
    }

    override fun getLanguage(): StateFlow<AppLanguage> = languageFlow.asStateFlow()

    override fun setLanguage(language: AppLanguage) {
        languageFlow.value = language
        scope.launch {
            try {
                PassGenDatabase.Schema.create(driver).await()
                queries.updateLanguage(language.code)
            } catch (_: Exception) {
            }
        }
    }

    override fun getTheme(): StateFlow<AppTheme> = themeFlow.asStateFlow()

    override fun setTheme(theme: AppTheme) {
        themeFlow.value = theme
        scope.launch {
            try {
                PassGenDatabase.Schema.create(driver).await()
                queries.updateTheme(theme.storageKey)
            } catch (_: Exception) {
            }
        }
    }

    override fun isCopyEnabled(): StateFlow<Boolean> = copyEnabledFlow.asStateFlow()

    override fun setCopyEnabled(enabled: Boolean) {
        copyEnabledFlow.value = enabled
        scope.launch {
            try {
                PassGenDatabase.Schema.create(driver).await()
                queries.updateCopyEnabled(if (enabled) 1L else 0L)
            } catch (_: Exception) {
            }
        }
    }

    override fun getVisibilityTimeout(): StateFlow<PasswordVisibilityTimeout> =
        visibilityTimeoutFlow.asStateFlow()

    override fun setVisibilityTimeout(timeout: PasswordVisibilityTimeout) {
        visibilityTimeoutFlow.value = timeout
        scope.launch {
            try {
                PassGenDatabase.Schema.create(driver).await()
                queries.updateVisibilityTimeout(timeout.storageKey)
            } catch (_: Exception) {
            }
        }
    }
}
