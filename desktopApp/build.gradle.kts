import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(project(":composeApp"))
    implementation(project(":core:ui"))

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)
    implementation(libs.compose.components.resources)
}

val appVersionCore = rootProject.extra["appVersionCore"] as String
val appVersionCode = rootProject.extra["appVersionCode"] as Int

// macOS requires MAJOR > 0
val macVersion = appVersionCore.split(".").let { (major, minor, patch) ->
    if (major == "0") "1.$minor.$patch" else appVersionCore
}

compose.desktop {
    application {
        mainClass = "ir.ornix.passgen.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ir.ornix.passgen"

            packageVersion = appVersionCore          // Windows (MSI/EXE) and Linux

            macOS {
                iconFile.set(project.file("icons/icon.icns"))

                dmgPackageVersion = macVersion
                packageVersion = macVersion
                packageBuildVersion = appVersionCode.toString() // CFBundleVersion
            }
            windows {
                iconFile.set(project.file("icons/icon.ico"))
            }
            linux {
                iconFile.set(project.file("icons/icon.png"))
            }
        }
    }
}