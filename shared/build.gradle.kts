plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.mokkery)
    alias(libs.plugins.kover)
}

kover {
    reports {
        filters {
            excludes {
                // Generated SQLDelight code
                classes(
                    // SQLDelight generated (top-level cz.krokviak.kalky package)
                    "cz.krokviak.kalky.core.db.*",
                    "cz.krokviak.kalky.Food_items*",
                    "cz.krokviak.kalky.Nutrient_settings*",
                    "cz.krokviak.kalky.Personal_info*",
                    "cz.krokviak.kalky.FoodItemQueries*",
                    "cz.krokviak.kalky.NutrientSettingQueries*",
                    "cz.krokviak.kalky.PersonalInfoQueries*",
                    // Auto-generated build/resource artifacts
                    "cz.krokviak.kalky.BuildConfig",
                    "cz.krokviak.kalky.shared.generated.*",
                    "*\$\$serializer",
                    "*ComposableSingletons*",
                    // Compose UI: top-level Composable scenes & their inner Composable closures
                    "*SceneKt",
                    "*SceneKt\$*",
                    "*PageKt",
                    "*PageKt\$*",
                    // Reusable UI components, theme, navigation glue
                    "cz.krokviak.kalky.core.ui.*",
                    "cz.krokviak.kalky.core.theme.*",
                    "cz.krokviak.kalky.core.app.*",
                    "cz.krokviak.kalky.scenes.*.components.*",
                    "cz.krokviak.kalky.scenes.onboarding.pages.*",
                )
            }
        }
    }
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    metricsDestination = layout.buildDirectory.dir("compose_compiler")
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
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

            // Immutable collections (Compose stability)
            implementation(libs.kotlinx.collections.immutable)

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

            // Settings (multiplatform SharedPreferences/NSUserDefaults)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.no.arg)

            // UI libraries (already multiplatform)
            implementation(libs.coil.compose)
            implementation(libs.compose.charts)
            implementation(libs.vico.multiplatform)
            implementation(compose.materialIconsExtended)
        }

        androidMain.dependencies {
            // Ktor engine
            implementation(libs.ktor.client.okhttp)

            // SQLDelight driver
            implementation(libs.sqldelight.android.driver)

            // Koin Android
            implementation(libs.koin.android)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.ktor.client.mock)
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(libs.sqldelight.sqlite.driver)
            }
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
    namespace = "cz.krokviak.kalky.shared"
    compileSdk = 35

    defaultConfig {
        minSdk = 30
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

sqldelight {
    databases {
        create("KalkyDatabase") {
            packageName.set("cz.krokviak.kalky.core.db")
        }
    }
}
