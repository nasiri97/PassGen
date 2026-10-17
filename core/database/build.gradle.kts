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

    jvm()

    android {
        namespace = "ir.ornix.passgen.core.database"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.sqldelight.coroutines)
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

        jsMain {
            dependencies {
                implementation(libs.sqldelight.driver.webworker)
                implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.0.2"))
                implementation(npm("sql.js", "1.8.0"))
            }
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
        }
    }
}
