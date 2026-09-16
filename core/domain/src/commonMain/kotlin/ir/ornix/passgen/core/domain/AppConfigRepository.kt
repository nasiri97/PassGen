package ir.ornix.passgen.core.domain

import kotlinx.coroutines.flow.StateFlow

interface AppConfigRepository {
    fun isFirstLaunch(): StateFlow<Boolean>
    fun setFirstLaunch(isFirstLaunch: Boolean)
}