package ir.ornix.passgen.core.domain

import ir.ornix.passgen.core.model.InstalledAppInfo

interface InstalledAppsProvider {
    fun isSupported(): Boolean
    suspend fun getInstalledApps(): List<InstalledAppInfo>
}
