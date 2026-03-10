plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Compose Multiplatform
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)

            // Coroutines
            implementation(libs.kotlinx.coroutines.core)

            // Serialization
            implementation(libs.kotlinx.serialization.json)

            // DateTime
            implementation(libs.kotlinx.datetime)

            // Ktor
            api(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            // SQLDelight
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)

            // Lifecycle (ViewModel KMP)
            api(libs.lifecycle.viewmodel)

            // Navigation (Compose Multiplatform)
            api(libs.navigation.compose.kmp)

            // DI
            implementation(libs.insert.koin.koin.core)

            // UI libraries (already multiplatform)
            implementation(libs.github.cupertino)
            implementation(libs.coil.compose)
            implementation(libs.compose.charts)
            implementation(libs.vico.multiplatform)
        }

        androidMain.dependencies {
            // Ktor engine
            implementation(libs.ktor.client.okhttp)

            // SQLDelight driver
            implementation(libs.sqldelight.android.driver)

            // Koin Android
            implementation(libs.koin.android)
        }

        iosMain.dependencies {
            // Ktor engine
            implementation(libs.ktor.client.darwin)

            // SQLDelight driver
            implementation(libs.sqldelight.native.driver)
        }
    }
}

android {
    namespace = "cz.krokviak.kalai.shared"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

sqldelight {
    databases {
        create("KalaiDatabase") {
            packageName.set("cz.krokviak.kalai.db")
        }
    }
}
