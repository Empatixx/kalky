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

                classes(

                    "cz.krokviak.kalky.core.db.*",
                    "cz.krokviak.kalky.Food_items*",
                    "cz.krokviak.kalky.Nutrient_settings*",
                    "cz.krokviak.kalky.Personal_info*",
                    "cz.krokviak.kalky.FoodItemQueries*",
                    "cz.krokviak.kalky.NutrientSettingQueries*",
                    "cz.krokviak.kalky.PersonalInfoQueries*",

                    "cz.krokviak.kalky.BuildConfig",
                    "cz.krokviak.kalky.shared.generated.*",
                    "*\$\$serializer",
                    "*ComposableSingletons*",

                    "*SceneKt",
                    "*SceneKt\$*",
                    "*PageKt",
                    "*PageKt\$*",

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

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)

            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.kotlinx.serialization.json)

            implementation(libs.kotlinx.collections.immutable)

            implementation(libs.kotlinx.datetime)

            api(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)

            api(libs.lifecycle.viewmodel)

            api(libs.navigation.compose.kmp)

            implementation(libs.insert.koin.koin.core)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.no.arg)

            implementation(libs.coil.compose)
            implementation(libs.compose.charts)
            implementation(libs.vico.multiplatform)
            implementation(compose.materialIconsExtended)
        }

        androidMain.dependencies {

            implementation(libs.ktor.client.okhttp)

            implementation(libs.sqldelight.android.driver)

            implementation(libs.koin.android)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.ktor.client.mock)

            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(libs.sqldelight.sqlite.driver)

                implementation(libs.robolectric)
            }
        }

        iosMain.dependencies {

            implementation(libs.ktor.client.darwin)

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

    testOptions {
        unitTests.isIncludeAndroidResources = true
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
