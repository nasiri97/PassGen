package ir.ornix.passgen.core.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import ir.ornix.passgen.core.domain.AppConfigRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsAppConfigRepository(private val settings: Settings = Settings()) :
    AppConfigRepository {

    companion object {
        private const val KEY = "first_launch"
        private const val DEFAULT_IS_FIRST_LAUNCH = true
    }

    private val isFirstLaunchStateFlow =
        MutableStateFlow<Boolean>(settings.getBoolean(KEY, DEFAULT_IS_FIRST_LAUNCH))

    override fun isFirstLaunch(): StateFlow<Boolean> {
        return isFirstLaunchStateFlow
    }

    override fun setFirstLaunch(isFirstLaunch: Boolean) {
        settings[KEY] = isFirstLaunch
        isFirstLaunchStateFlow.value = isFirstLaunch
    }
}