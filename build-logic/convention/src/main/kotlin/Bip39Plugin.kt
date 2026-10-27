import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

abstract class Bip39Extension {
    abstract val packageName: Property<String>
    abstract val expectedSha256: Property<String>
    abstract val wordListPath: Property<String>
}

class Bip39Plugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val extension = extensions.create<Bip39Extension>("bip39").apply {
                wordListPath.convention("src/commonMain/resources/bip39/bip39-english.txt")
            }

            val generate = tasks.register<GenerateBip39WordListTask>("generateBip39WordList") {
                group = "code generation"
                description =
                    "Validates the BIP-39 English word list and generates the Kotlin word array."
                wordListFile.set(layout.projectDirectory.file(extension.wordListPath))
                expectedSha256.set(extension.expectedSha256)
                packageName.set(extension.packageName)
                outputDir.set(layout.buildDirectory.dir("generated/bip39/commonMain/kotlin"))
            }

            pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
                extensions.configure<KotlinMultiplatformExtension> {
                    // srcDir from the task output also makes every compile task depend on it
                    sourceSets.getByName("commonMain").kotlin.srcDir(generate.flatMap { it.outputDir })
                }
            }
        }
    }
}