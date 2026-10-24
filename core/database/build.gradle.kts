import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.sqldelight)
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
        namespace = "ir.ornix.passgen.core.database"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":core:common"))
                implementation(libs.sqldelight.coroutines)
                implementation(libs.sqldelight.async)
                implementation(libs.sqldelight.primitive.adapters)
                implementation(libs.kotlinx.coroutines.core)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.sqldelight.driver.android)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.sqldelight.driver.sqlite)
            }
        }

        val webMain = create("webMain") {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.sqldelight.driver.webworker)
                implementation(libs.wrappers.browser)
                implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.1.0"))
                implementation(npm("sql.js", "1.8.0"))
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
            dependencies {
                implementation(libs.sqldelight.driver.native)
            }
        }

        iosArm64Main {
            dependsOn(iosMain)
        }

        iosSimulatorArm64Main {
            dependsOn(iosMain)
        }
    }
}

sqldelight {
    databases {
        create("PassGenDatabase") {
            packageName.set("ir.ornix.passgen.core.database")
            generateAsync.set(true)
        }
    }
}
