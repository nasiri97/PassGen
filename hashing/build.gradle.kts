@file:OptIn(ExperimentalWasmDsl::class)

import org.gradle.kotlin.dsl.project
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
}

kotlin {

    jvm()
    androidLibrary {
        namespace = "ir.ornix.passgen.hashing"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
//    iosArm64()
//    iosSimulatorArm64()
//    js { browser() }
//    wasmJs { browser() }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":codec"))

            // Coroutine
            implementation(libs.kotlinx.coroutines.core)

            // Bcrypt
            implementation(libs.jbcrypt)

            // Argon2
            implementation(libs.bcprov)

            implementation(libs.cryptography.core)
            implementation(libs.cryptography.provider.optimal)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}