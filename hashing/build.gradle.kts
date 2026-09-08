import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
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
        namespace = "ir.ornix.passgen.hashing"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":codec"))

            // Coroutine
            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.cryptography.core)
            implementation(libs.cryptography.provider.optimal)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }

        val jvmCommonMain = create("jvmCommonMain") {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.bcprov)
            }
        }

        androidMain {
            dependsOn(jvmCommonMain)
        }

        jvmMain {
            dependsOn(jvmCommonMain)
        }
    }
}