import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import java.security.MessageDigest
import java.time.Duration

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
}

private val bip39WordListFile = layout.projectDirectory.file(
    "src/commonMain/resources/bip39/bip39-english.txt"
)


// Generated file : core/common/build/generated/bip39/commonMain/kotlin/
// ir/ornix/passgen/core/common/codec/util/Bip39EnglishWordList.kt
private val generatedBip39Dir = layout.buildDirectory.dir(
    "generated/bip39/commonMain/kotlin"
)

private val EXPECTED_BIP39_ENGLISH_SHA256 =
    "2f5eed53a4727b4bf8880d8f3f199efc90e58503646d9ff8eff3a2ed3b24dbda"

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
                implementation(project(":log-core"))
                implementation(libs.kotlin.stdlib)

                // Kotlin Serialization
                implementation(libs.kotlinx.serialization.json)

                // Coroutine
                implementation(libs.kotlinx.coroutines.core)

                implementation(libs.cryptography.core)
                implementation(libs.cryptography.provider.optimal)
            }

            // Because the generated Kotlin file isn't inside src/commonMain/kotlin, telling Kotlin Multiplatform about it
            kotlin.srcDir(generatedBip39Dir)
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


val generateBip39WordList = tasks.register("generateBip39WordList") {
    group = "code generation"
    description = "Validates the BIP-39 English word list and generates the Kotlin word array."

    val inputFile = bip39WordListFile.asFile
    val outputDir = generatedBip39Dir.get().asFile

    inputs.file(inputFile)
    outputs.dir(outputDir)

    doLast {
        require(inputFile.exists()) {
            "BIP-39 English word list not found: ${inputFile.absolutePath}"
        }

        // ------------------------------------------------------------
        // Verify the exact official file
        // ------------------------------------------------------------

        val actualSha256 = MessageDigest
            .getInstance("SHA-256")
            .digest(inputFile.readBytes())
            .toHexString()

        require(
            actualSha256.equals(
                EXPECTED_BIP39_ENGLISH_SHA256,
                ignoreCase = true
            )
        ) {
            """
            BIP-39 English word list checksum mismatch!

            Expected:
            $EXPECTED_BIP39_ENGLISH_SHA256

            Actual:
            $actualSha256

            File:
            ${inputFile.absolutePath}
            """.trimIndent()
        }

        // ------------------------------------------------------------
        // Parse and validate words
        // ------------------------------------------------------------

        val words = inputFile
            .readLines()
            .map(String::trim)
            .filter(String::isNotEmpty)

        require(words.size == 2048) {
            "BIP-39 English word list must contain exactly 2048 words. " +
                    "Found: ${words.size}"
        }

        require(words.distinct().size == words.size) {
            "BIP-39 English word list contains duplicate words."
        }

        require(words.all { word ->
            word.all { char -> char in 'a'..'z' }
        }) {
            "BIP-39 English word list must contain lowercase ASCII words only."
        }

        // ------------------------------------------------------------
        // Generate Kotlin source
        // ------------------------------------------------------------

        val packageName =
            "ir.ornix.passgen.core.common.codec.util"

        val outputFile = File(
            outputDir,
            "ir/ornix/passgen/core/common/codec/util/Bip39EnglishWordList.kt"
        )

        outputFile.parentFile.mkdirs()

        outputFile.writeText(
            buildString {
                appendLine("package $packageName")
                appendLine()
                appendLine("/**")
                appendLine(" * Official BIP-39 English word list.")
                appendLine(" *")
                appendLine(" * SHA-256:")
                appendLine(" * $actualSha256")
                appendLine(" *")
                appendLine(" * Generated from:")
                appendLine(" * src/commonMain/resources/bip39/english.txt")
                appendLine(" *")
                appendLine(" * DO NOT EDIT MANUALLY.")
                appendLine(" */")
                appendLine(
                    "internal val BIP39_ENGLISH_WORDS: Array<String> = arrayOf("
                )

                words.forEach { word ->
                    appendLine("    \"$word\",")
                }

                appendLine(")")
            }
        )

        logger.lifecycle(
            "Generated BIP-39 word list: ${outputFile.absolutePath}"
        )
    }
}

private fun ByteArray.toHexString(): String =
    joinToString("") { byte ->
        "%02x".format(byte.toInt() and 0xFF)
    }

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>()
    .configureEach {
        dependsOn(generateBip39WordList)
    }
