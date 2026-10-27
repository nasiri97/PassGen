import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

abstract class AppVersionExtension {
    /** Package of the generated `AppVersion` object, e.g. ir.ornix.passgen.core.appinfo */
    abstract val packageName: Property<String>
}

class AppVersionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val extension = extensions.create<AppVersionExtension>("appVersion")
            val info = appVersionInfo()

            val generate = tasks.register<GenerateAppVersionTask>("generateAppVersion") {
                group = "code generation"
                description = "Generates the AppVersion object from app.versionName."
                versionName.set(info.map { it.name })
                versionCode.set(info.map { it.code })
                packageName.set(extension.packageName)
                outputDir.set(layout.buildDirectory.dir("generated/appVersion/kotlin"))

                // Keep iosApp/Configuration/Version.xcconfig fresh on every build
                dependsOn(rootProject.tasks.matching { it.name == "syncIosVersion" })
            }

            pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
                extensions.configure<KotlinMultiplatformExtension> {
                    // srcDir from the task output also makes every compile task depend on it
                    sourceSets.getByName("commonMain")
                        .kotlin.srcDir(generate.flatMap { it.outputDir })
                }
            }
        }
    }
}