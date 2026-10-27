plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("appVersionRoot") {
            id = "app.version.root"
            implementationClass = "AppVersionRootPlugin"
        }
        register("appVersion") {
            id = "app.version"
            implementationClass = "AppVersionPlugin"
        }
        register("bip39") {
            id = "app.bip39"
            implementationClass = "Bip39Plugin"
        }
    }
}