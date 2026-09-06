plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvm()
    android {
        namespace = "ir.ornix.passgen.feature.home.impl"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(project(":log-core"))
            implementation(project(":codec"))
            implementation(project(":hashing"))
            implementation(project(":passwordGenerator"))
            implementation(project(":core:domain"))
            implementation(project(":feature:home:api"))
            implementation(project(":core:data"))
            api(project(":core:designsystem"))

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }
    }
}
