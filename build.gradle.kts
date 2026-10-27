import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.androidLint) apply false
    alias(libs.plugins.sqldelight) apply false

    // build-logic plugins
    alias(libs.plugins.app.root.version)   // version values + syncIosVersion
    alias(libs.plugins.app.version) apply false
    alias(libs.plugins.app.bip39) apply false
}

subprojects {
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure<KotlinMultiplatformExtension> {
            if (targets.findByName("js") == null) {
                js {
                    browser()
                }
            }
            if (targets.findByName("wasmJs") == null) {
                @OptIn(ExperimentalWasmDsl::class)
                wasmJs {
                    browser()
                }
            }
        }
    }

    tasks.matching { it.name.startsWith("checkComposeUiTestConfiguration") }.configureEach {
        enabled = false
    }

    tasks.matching { it.name.endsWith("BrowserTest") }.configureEach {
        enabled = project.hasProperty("enableBrowserTests")
        val subprojectKarmaDir = project.file("karma.config.d")
        doFirst {
            val rootKarmaDir = rootProject.file("karma.config.d")
            if (rootKarmaDir.exists()) {
                rootKarmaDir.copyRecursively(subprojectKarmaDir, overwrite = true)
            }
        }
        doLast {
            if (subprojectKarmaDir.exists()) {
                subprojectKarmaDir.deleteRecursively()
            }
        }
    }
}