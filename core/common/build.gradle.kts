import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import java.time.Duration

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {

    iosArm64()
    iosSimulatorArm64()

    js {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
                timeout.set(Duration.ofMinutes(5))
            }
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
                timeout.set(Duration.ofMinutes(5))
            }
        }
    }

    jvm()

    android {
        namespace = "ir.ornix.passgen.core.common"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    sourceSets {

        commonMain {
            dependencies {
                implementation(project(":log-core"))
                implementation(libs.kotlin.stdlib)

                // Kotlin Serialization
                implementation(libs.kotlinx.serialization.json)


                // Coroutine
                implementation(libs.kotlinx.coroutines.core)

                implementation(libs.cryptography.core)
                implementation(libs.cryptography.provider.optimal)
            }
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

        val webMain = create("webMain") {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.wrappers.browser)
                implementation(npm("hash-wasm", "4.12.0"))
            }
        }

        jsMain {
            dependsOn(webMain)
        }

        wasmJsMain {
            dependsOn(webMain)
        }

        val iosMain = create("iosMain") {
            dependsOn(commonMain.get())
        }

        iosArm64Main {
            dependsOn(iosMain)
        }

        iosSimulatorArm64Main {
            dependsOn(iosMain)
        }
    }
}
