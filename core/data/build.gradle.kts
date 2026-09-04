plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
}

kotlin {
    iosArm64()
    iosSimulatorArm64()
    jvm()
    android {
        namespace = "ir.ornix.passgen.core.data"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:domain"))
            implementation(libs.multiplatform.settings)
        }
    }
}
