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
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    jvm()

    android {
        namespace = "ir.ornix.passgen.feature.home.impl"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        androidResources {
            enable = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:home:api"))
            api(project(":core:ui"))
            implementation(project(":log-core"))
            implementation(project(":core:domain"))

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }

        val androidDeviceTest by getting {
            dependencies {
                implementation(libs.androidx.uitest.junit4)
                implementation(libs.androidx.uitest.manifest)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.cryptography.core)
                implementation(libs.cryptography.provider.optimal)
                implementation(libs.koin.test)


                // Force a newer espresso-core version to fix the InputManager crash on modern emulators
                implementation(libs.androidx.espresso.core)
            }
        }
    }
}
