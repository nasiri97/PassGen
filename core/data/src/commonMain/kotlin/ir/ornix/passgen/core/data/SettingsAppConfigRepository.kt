package ir.ornix.passgen.core.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import ir.ornix.passgen.core.domain.AppConfigRepository

class SettingsAppConfigRepository(private val settings: Settings = Settings()) :
    AppConfigRepository {

    companion object {
        private const val KEY = "first_launch"
        private const val DEFAULT_IS_FIRST_LAUNCH = true
    }


    override fun isFirstLaunch(): Boolean {
        return settings.getBoolean(KEY, DEFAULT_IS_FIRST_LAUNCH)
    }

    override fun setFirstLaunch(isFirstLaunch: Boolean) {
        settings[KEY] = isFirstLaunch
    }
}