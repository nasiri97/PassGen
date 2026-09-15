package ir.ornix.passgen.core.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import ir.ornix.passgen.core.domain.AppConfigRepository

class SettingsAppConfigRepository(private val settings: Settings = Settings()) :
    AppConfigRepository {

    companion object {
        private const val KEY_FIRST_LAUNCH = "first_launch"
    }

    override fun isFirstLaunch(): Boolean {
        return settings.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    override fun setFirstLaunch(isFirstLaunch: Boolean) {
        settings[KEY_FIRST_LAUNCH] = isFirstLaunch
    }
}