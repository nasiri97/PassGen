package ir.ornix.passgen

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import ir.ornix.passgen.RootDetector.isDeviceRooted
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * Heuristic root detection for Android.
 *
 * NOTE: Call [isDeviceRooted] from a background thread (Dispatchers.IO),
 * because some checks spawn processes and read files.
 *
 * For Android 11+ (API 30+), add this to AndroidManifest.xml so the
 * root-app package check can see those packages:
 *
 * <queries>
 *     <package android:name="com.topjohnwu.magisk" />
 *     <package android:name="eu.chainfire.supersu" />
 *     <package android:name="com.koushikdutta.superuser" />
 *     <package android:name="com.noshufou.android.su" />
 *     <package android:name="com.thirdparty.superuser" />
 *     <package android:name="com.yellowes.su" />
 *     <package android:name="com.zachspong.temprootremovejb" />
 *     <package android:name="com.ramdroid.appquarantine" />
 *     <package android:name="me.weishu.kernelsu" />
 * </queries>
 */
object RootDetector {

    fun isDeviceRooted(context: Context): Boolean = runCatching {
        hasTestKeys() ||
                hasSuBinary() ||
                canExecuteSu() ||
                hasRootManagementApps(context) ||
                hasDangerousProps() ||
                hasWritableSystemPartitions() ||
                hasMagiskTraces()
    }.getOrDefault(false)

    // 1. Build signed with test keys (custom ROMs / rooted images)
    private fun hasTestKeys(): Boolean =
        Build.TAGS?.contains("test-keys") == true

    // 2. Known su binary locations
    private fun hasSuBinary(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su",
            "/su/bin",
            "/system/xbin/daemonsu",
            "/system/etc/init.d/99SuperSUDaemon",
            "/system/bin/.ext/.su",
            "/system/usr/we-need-root/su-backup",
            "/system/xbin/mu"
        )
        return paths.any { path ->
            runCatching { File(path).exists() }.getOrDefault(false)
        }
    }

    // 3. Try to locate / execute su
    private fun canExecuteSu(): Boolean {
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("which", "su"))
            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                !reader.readLine().isNullOrBlank()
            }
        } catch (_: Throwable) {
            false
        } finally {
            runCatching { process?.destroy() }
        }
    }

    // 4. Installed root management apps
    @Suppress("DEPRECATION")
    private fun hasRootManagementApps(context: Context): Boolean {
        val packages = arrayOf(
            "com.topjohnwu.magisk",
            "io.github.vvb2060.magisk",
            "me.weishu.kernelsu",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.noshufou.android.su",
            "com.noshufou.android.su.elite",
            "com.thirdparty.superuser",
            "com.yellowes.su",
            "com.zachspong.temprootremovejb",
            "com.ramdroid.appquarantine",
            "com.devadvance.rootcloak",
            "com.devadvance.rootcloakplus",
            "de.robv.android.xposed.installer",
            "com.saurik.substrate",
            "com.amphoras.hidemyroot",
            "com.formyhm.hiderootPremium"
        )
        val pm = context.packageManager
        return packages.any { pkg ->
            try {
                pm.getPackageInfo(pkg, 0)
                true
            } catch (_: PackageManager.NameNotFoundException) {
                false
            } catch (_: Throwable) {
                false
            }
        }
    }

    // 5. Dangerous system properties
    private fun hasDangerousProps(): Boolean {
        val debuggable = getSystemProp("ro.debuggable")
        val secure = getSystemProp("ro.secure")
        return debuggable == "1" || secure == "0"
    }

    private fun getSystemProp(name: String): String? {
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("getprop", name))
            BufferedReader(InputStreamReader(process.inputStream)).use { it.readLine()?.trim() }
        } catch (_: Throwable) {
            null
        } finally {
            runCatching { process?.destroy() }
        }
    }

    // 6. System partitions mounted read-write
    private fun hasWritableSystemPartitions(): Boolean {
        val sensitiveMounts = setOf(
            "/system", "/system/bin", "/system/sbin", "/system/xbin",
            "/vendor/bin", "/sbin", "/etc", "/product"
        )
        return try {
            File("/proc/mounts").useLines { lines ->
                lines.any { line ->
                    val parts = line.split(" ")
                    if (parts.size < 4) return@any false
                    val mountPoint = parts[1]
                    val options = parts[3].split(",")
                    mountPoint in sensitiveMounts && options.contains("rw")
                }
            }
        } catch (_: Throwable) {
            false
        }
    }

    // 7. Magisk / KernelSU leftovers
    private fun hasMagiskTraces(): Boolean {
        val paths = arrayOf(
            "/sbin/.magisk",
            "/sbin/.core/mirror",
            "/sbin/.core/img",
            "/sbin/.core/db-0/magisk.db",
            "/data/adb/magisk",
            "/data/adb/modules",
            "/data/adb/ksu",
            "/cache/.disable_magisk",
            "/dev/.magisk.unique"
        )
        return paths.any { path ->
            runCatching { File(path).exists() }.getOrDefault(false)
        }
    }
}