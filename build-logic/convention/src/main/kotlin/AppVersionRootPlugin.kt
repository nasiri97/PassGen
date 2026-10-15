import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.register

class AppVersionRootPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            if (this != rootProject) {
                throw GradleException("app.version.root must be applied to the root project only")
            }

            val info = appVersionInfo().orNull
                ?: throw GradleException("Missing 'app.versionName' in gradle.properties")

            // Same keys as before, so androidApp / desktopApp / webApp scripts stay unchanged
            extra["appVersionName"] = info.name
            extra["appVersionCode"] = info.code
            extra["appVersionCore"] = info.core
            extra["appVersionSemver"] = info.semver

            tasks.register<SyncIosVersionTask>("syncIosVersion") {
                group = "versioning"
                description = "Writes iosApp/Configuration/Version.xcconfig from app.versionName."
                fullName.set(info.name)
                core.set(info.core)
                code.set(info.code)
                xcconfig.set(layout.projectDirectory.file("iosApp/Configuration/Version.xcconfig"))
            }
        }
    }
}