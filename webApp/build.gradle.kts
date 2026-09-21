import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

val isDebugWebBuild =
    providers.gradleProperty("debugBuild")
        .map(String::toBoolean)
        .getOrElse(false)


// webApp/build/generated/webMain/resources/build-config.js
val generatedWebResources =
    layout.buildDirectory.dir("generated/webMain/resources")


/*
 * Generates the web build configuration used by the JavaScript/Wasm targets.
 *
 * The generated file is:
 *
 *   build/generated/webMain/resources/build-config.js
 *
 * It is consumed by index.html:
 *
 *   <script src="build-config.js"></script>
 *
 * The debug flag is controlled explicitly using the `debugBuild` Gradle property.
 * It defaults to `false` when the property is not provided.
 *
 *
 * Development build:
 *
 *   JS:
 *     ./gradlew :webApp:jsBrowserDevelopmentRun
 *     ./gradlew :webApp:jsBrowserDevelopmentRun -PdebugBuild=true
 *
 *   Wasm:
 *     ./gradlew :webApp:wasmJsBrowserDevelopmentRun
 *     ./gradlew :webApp:wasmJsBrowserDevelopmentRun -PdebugBuild=true
 *
 *
 * Production distribution:
 *
 *   JS:
 *     ./gradlew :webApp:jsBrowserProductionExecutableDistribution
 *     ./gradlew :webApp:jsBrowserProductionExecutableDistribution -PdebugBuild=true
 *
 *   Wasm:
 *     ./gradlew :webApp:wasmJsBrowserDistribution
 *     ./gradlew :webApp:wasmJsBrowserDistribution -PdebugBuild=true
 *
 *
 * Without `-PdebugBuild=true`:
 *
 *   globalThis.__APP_DEBUG__ = false;
 *
 * With `-PdebugBuild=true`:
 *
 *   globalThis.__APP_DEBUG__ = true;
 *
 *
 * The generated file is located under `build/`, so it is not committed to Git.
 */
val generateWebBuildConfig = tasks.register("generateWebBuildConfig") {
    description = "Generates the web build configuration file."
    group = "build"

    outputs.dir(generatedWebResources)

    doLast {
        val directory = generatedWebResources.get().asFile
        directory.mkdirs()

        directory.resolve("build-config.js").writeText(
            """
            globalThis.__APP_DEBUG__ = $isDebugWebBuild;
            """.trimIndent()
        )
    }
}

kotlin {
    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        val webMain = create("webMain") {
            dependsOn(commonMain.get())

            resources.srcDir(generatedWebResources)

            dependencies {
                implementation(project(":composeApp"))
                implementation(libs.compose.ui)
            }
        }

        jsMain {
            dependsOn(webMain)
        }

        wasmJsMain {
            dependsOn(webMain)
        }
    }
}

/*
 * Make web distribution/executable tasks generate build-config.js first.
 */
tasks.configureEach {
    if (
        name.contains("Browser", ignoreCase = true) &&
        (
                name.contains("Run", ignoreCase = true) ||
                        name.contains("Distribution", ignoreCase = true) ||
                        name.contains("Executable", ignoreCase = true)
                )
    ) {
        dependsOn(generateWebBuildConfig)
    }
}