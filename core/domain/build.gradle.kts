plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
}

kotlin {
    iosArm64()
    iosSimulatorArm64()
    jvm()
    android {
        namespace = "ir.ornix.passgen.core.domain"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }
}
