import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvm()
    android {
       namespace = "ir.ornix.passgen.composeapp"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
    }
    
    sourceSets {
        commonMain.dependencies {
            api(project(":core:designsystem"))
            implementation(project(":core:domain"))
            implementation(project(":core:data"))
            implementation(project(":feature:home:api"))
            implementation(project(":feature:home:impl"))
            implementation(project(":feature:about:api"))
            implementation(project(":feature:about:impl"))
            implementation(project(":feature:saved-passwords:api"))
            implementation(project(":feature:saved-passwords:impl"))
            implementation(project(":feature:settings:api"))
            implementation(project(":feature:settings:impl"))
            implementation(project(":feature:setup:api"))
            implementation(project(":feature:setup:impl"))

            implementation(libs.compose.icons)
            
            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.androidx.navigation3.runtime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.multiplatform.settings)
            
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }
    }
}
