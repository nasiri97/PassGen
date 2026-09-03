plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    iosArm64()
    iosSimulatorArm64()
    jvm()
    android {
        namespace = "ir.ornix.passgen.feature.settings.api"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }
    
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.serialization.json)
            api(libs.androidx.navigation3.runtime)
        }
    }
}
