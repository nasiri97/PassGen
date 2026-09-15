package ir.ornix.passgen.core.domain

interface AppConfigRepository {
    fun isFirstLaunch(): Boolean
    fun setFirstLaunch(isFirstLaunch: Boolean)
}