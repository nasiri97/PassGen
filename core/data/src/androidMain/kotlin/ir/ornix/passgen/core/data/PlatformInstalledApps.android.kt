package ir.ornix.passgen.core.data

import android.content.Intent
import androidx.core.graphics.drawable.toBitmap
import ir.ornix.passgen.core.common.AppContextProvider
import ir.ornix.passgen.core.domain.InstalledAppsProvider
import ir.ornix.passgen.core.model.InstalledAppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidInstalledAppsProvider : InstalledAppsProvider {
    override fun isSupported(): Boolean = true

    override suspend fun getInstalledApps(): List<InstalledAppInfo> = withContext(Dispatchers.IO) {
        val context = AppContextProvider.applicationContext ?: return@withContext emptyList()
        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
        val myPackageName = context.packageName

        resolveInfos
            .mapNotNull { resolveInfo ->
                val packageName = resolveInfo.activityInfo.packageName
                if (packageName == myPackageName) return@mapNotNull null

                val appName = resolveInfo.loadLabel(pm).toString()
                val iconDrawable = try {
                    resolveInfo.loadIcon(pm)
                } catch (_: Exception) {
                    null
                }

                val bitmap = iconDrawable?.let { drawable ->
                    try {
                        drawable.toBitmap(width = 96, height = 96)
                    } catch (_: Exception) {
                        null
                    }
                }

                InstalledAppInfo(
                    appName = appName,
                    packageName = packageName,
                    icon = bitmap
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.appName.lowercase() }
    }
}

actual fun getPlatformInstalledAppsProvider(): InstalledAppsProvider = AndroidInstalledAppsProvider()
