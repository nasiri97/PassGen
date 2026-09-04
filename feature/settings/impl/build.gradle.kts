plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    iosArm64()
    iosSimulatorArm64()
    jvm()
    android {
        namespace = "ir.ornix.passgen.feature.settings.impl"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:settings:api"))
            implementation(project(":core:designsystem"))
            
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
    }
}
