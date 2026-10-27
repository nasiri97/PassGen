import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.provider.Provider

/**
 * Single place that understands `app.versionName` and derives every platform value from it.
 *
 *  name   -> as written, e.g. 1.0.0.alpha.1        (Android versionName, AppVersion.NAME)
 *  code   -> monotonic integer, e.g. 10_000_101    (Android versionCode, iOS build number, AppVersion.CODE)
 *  core   -> numeric only, e.g. 1.0.0              (iOS, desktop installers)
 *  semver -> npm-compatible, e.g. 1.0.0-alpha.1    (js / wasmJs package.json)
 */
data class AppVersionInfo(
    val name: String,
    val code: Int,
    val core: String,
    val semver: String
) {
    companion object {
        // MAJOR.MINOR.PATCH[(.|-)(alpha|beta|rc)[(.|-)N]]
        private val REGEX =
            Regex("""^(\d+)\.(\d+)\.(\d+)(?:[.-](alpha|beta|rc)(?:[.-]?(\d+))?)?$""")

        private const val MAX_PART = 99

        fun parse(name: String): AppVersionInfo {
            val match = REGEX.matchEntire(name) ?: throw GradleException(
                "app.versionName '$name' is invalid. " +
                    "Expected MAJOR.MINOR.PATCH or MAJOR.MINOR.PATCH.(alpha|beta|rc).N"
            )

            val (majorS, minorS, patchS, stage, preNumS) = match.destructured
            val major = majorS.toInt()
            val minor = minorS.toInt()
            val patch = patchS.toInt()
            val preNumber = preNumS.toIntOrNull() ?: 0

            if (maxOf(major, minor, patch, preNumber) > MAX_PART) {
                throw GradleException(
                    "Each version part (and the pre-release number) must be <= $MAX_PART"
                )
            }

            // alpha < beta < rc < final release
            val stageBase = when (stage) {
                "alpha" -> 100
                "beta" -> 200
                "rc" -> 300
                else -> 900 // final release
            }

            // 1.0.0.alpha.1 -> 10_000_101, 1.0.0 -> 10_000_900, 1.4.2 -> 10_402_900
            val code = (major * 10_000 + minor * 100 + patch) * 1_000 + stageBase + preNumber
            val core = "$major.$minor.$patch"

            // npm only accepts strict semver: MAJOR.MINOR.PATCH[-prerelease]
            //   1.0.0.alpha.1 -> 1.0.0-alpha.1 | 1.0.0-beta2 -> 1.0.0-beta.2 | 1.4.2 -> 1.4.2
            val semver =
                if (stage.isEmpty()) core
                else "$core-$stage" + if (preNumS.isNotEmpty()) ".$preNumber" else ""

            return AppVersionInfo(name = name, code = code, core = core, semver = semver)
        }
    }
}

/** Reads `app.versionName` from gradle.properties and parses it. Used by both plugins. */
fun Project.appVersionInfo(): Provider<AppVersionInfo> =
    providers.gradleProperty("app.versionName")
        .map { AppVersionInfo.parse(it) }