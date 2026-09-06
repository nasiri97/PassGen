@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {

    jvm()
    android {
        namespace = "ir.ornix.passgen.passwordgenerator"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
//    iosArm64()
//    iosSimulatorArm64()
//    js { browser() }
//    wasmJs { browser() }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":log-core"))
                implementation(project(":codec"))
                implementation(project(":hashing"))
                implementation(libs.kotlin.stdlib)

                // Kotlin Serialization
                implementation(libs.kotlinx.serialization.json)
            }
        }
    }

}