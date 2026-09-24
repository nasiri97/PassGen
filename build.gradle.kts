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
}

subprojects {
    tasks.matching { it.name.endsWith("BrowserTest") }.configureEach {
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

