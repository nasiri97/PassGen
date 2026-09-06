plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvm()
    android {
        namespace = "ir.ornix.passgen.core.data"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:domain"))
            implementation(project(":passwordGenerator"))
            implementation(libs.multiplatform.settings)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
