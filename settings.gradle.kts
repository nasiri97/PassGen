rootProject.name = "PassGen"

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":log-core")

include(":androidApp")
include(":desktopApp")
include(":webApp")
include(":composeApp")

include(":core:designsystem")
include(":core:ui")
include(":core:domain")
include(":core:data")
include(":core:model")
include(":core:common")

include(":feature:localauth:api", ":feature:localauth:impl")
include(":feature:home:api", ":feature:home:impl")
include(":feature:about:api", ":feature:about:impl")
include(":feature:saved-passwords:api", ":feature:saved-passwords:impl")
include(":feature:settings:api", ":feature:settings:impl")
