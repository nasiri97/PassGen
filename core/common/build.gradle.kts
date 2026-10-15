import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import java.time.Duration

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.app.version)
    alias(libs.plugins.app.bip39)
}

appVersion {
    packageName.set("ir.ornix.passgen.core.common")
}

bip39 {
    packageName.set("ir.ornix.passgen.core.common.codec.util")
    expectedSha256.set("2f5eed53a4727b4bf8880d8f3f199efc90e58503646d9ff8eff3a2ed3b24dbda")
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
                timeout.set(Duration.ofMinutes(15))
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
                timeout.set(Duration.ofMinutes(15))
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
                api(project(":core:logging"))
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
