import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {

    iosArm64()
    iosSimulatorArm64()

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    jvm()

    android {
        namespace = "ir.ornix.passgen.core.ui"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        androidResources {
            enable = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":core:designsystem"))
            api(project(":core:model"))
            api(project(":core:common"))
            api(libs.compose.components.resources)
        }

        val jvmCommonMain = create("jvmCommonMain") {
            dependsOn(commonMain.get())
        }

        androidMain {
            dependsOn(jvmCommonMain)
        }

        jvmMain {
            dependsOn(jvmCommonMain)
        }

        val webMain = create("webMain") {
            dependsOn(commonMain.get())
        }

        jsMain {
            dependsOn(webMain)
        }

        wasmJsMain {
            dependsOn(webMain)
        }

        val iosMain = create("iosMain") {
            dependsOn(commonMain.get())
        }

        iosArm64Main {
            dependsOn(iosMain)
        }

        iosSimulatorArm64Main {
            dependsOn(iosMain)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "ir.ornix.passgen.core.ui"
}
