import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)

    kotlin("plugin.serialization") version "2.2.20"

    alias(libs.plugins.protobuf)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)

    }

    jvm()

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.koin.core)
            implementation(libs.koin.android)

            implementation(libs.androidx.window)

            implementation(libs.ktor.client.okhttp)

        }

        androidUnitTest.dependencies {
        }

        androidInstrumentedTest.dependencies {
            implementation(libs.androidx.ui.test.junit4.android)
            implementation(libs.androidx.ui.test.manifest)
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.navigation.compose)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.kotlinx.serialization.json)


            implementation(libs.material3)

            implementation(libs.poi)
            implementation(libs.poi.ooxml)
            implementation(libs.commons.collections4)

            implementation(libs.coil.compose)
            implementation(libs.coil3.coil.network.okhttp)

            implementation(libs.io.ktor.ktor.client.core)
            implementation(libs.io.ktor.ktor.client.websockets)
            implementation(libs.ktor.client.okhttp)

            implementation(libs.ktor.server.core)
            implementation(libs.ktor.server.cio)
            implementation(libs.ktor.server.websockets)
            implementation(libs.ktor.server.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)


            implementation(libs.io.ktor.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.content.negotiation)


            implementation(libs.jmdns)

            implementation(compose.materialIconsExtended)

            implementation(libs.adaptive)
            implementation(libs.material3.adaptive.navigation.suite)
            implementation(libs.material3.window.size.class1)

            implementation(libs.androidx.datastore.preferences.v121)
            implementation(libs.androidx.datastore.core.v121)

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.koin.test)
            implementation(libs.kotlinx.coroutines.test)

            implementation(libs.mockito.core)
            implementation(libs.mockito.kotlin)
            implementation(libs.mockk)

            implementation(libs.turbine)

            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)


        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)

            implementation(libs.koin.core)

            implementation(libs.ktor.client.java)
            implementation(libs.ktor.client.cio)

        }

        jvmTest.dependencies {
            implementation(compose.desktop.currentOs)

        }
    }
}

android {
    namespace = "org.my.drivexcel"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.my.drivexcel"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = Versions.VERSION_CODE
        versionName = Versions.VERSION_NAME

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    testImplementation(libs.junit.junit)
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "org.my.drivexcel.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.my.drivexcel"
            packageVersion = Versions.VERSION_NAME
        }


    }
}



