package ir.ornix.passgen.core.data

import ir.ornix.passgen.core.domain.InstalledAppsProvider
import ir.ornix.passgen.core.model.InstalledAppInfo

private object NoOpInstalledAppsProvider : InstalledAppsProvider {
    override fun isSupported(): Boolean = false
    override suspend fun getInstalledApps(): List<InstalledAppInfo> = emptyList()
}

actual fun getPlatformInstalledAppsProvider(): InstalledAppsProvider = NoOpInstalledAppsProvider
