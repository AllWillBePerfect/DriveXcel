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

            implementation("androidx.window:window:1.5.0") // или новее

            implementation("io.ktor:ktor-client-okhttp:3.3.2")

        }

        androidUnitTest.dependencies {
        }

        androidInstrumentedTest.dependencies {
            implementation("androidx.compose.ui:ui-test-junit4-android:1.10.5")
            implementation("androidx.compose.ui:ui-test-manifest:1.10.5")
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

            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.navigation.compose)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            // Kotlinx serialization
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

//            implementation("androidx.compose.material3:material3:1.4.0")

            // В зависимостях (например, в модуле, который распространяется на общую / UI часть)
            implementation("org.jetbrains.compose.material3:material3:1.9.0")

            implementation("org.apache.poi:poi:5.4.1")        // для .xls
            implementation("org.apache.poi:poi-ooxml:5.4.1")  // для .xlsx
            implementation("org.apache.commons:commons-collections4:4.5.0") // иногда нужна для POI

            implementation("io.coil-kt.coil3:coil-compose:3.3.0")
            implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")

            // Ktor client
            implementation("io.ktor:ktor-client-core:3.3.2")
            implementation("io.ktor:ktor-client-websockets:3.3.2")
            implementation("io.ktor:ktor-client-okhttp:3.3.2")

            implementation("io.ktor:ktor-server-core:3.3.2")
            implementation("io.ktor:ktor-server-cio:3.3.2")
            implementation("io.ktor:ktor-server-websockets:3.3.2")
            implementation("io.ktor:ktor-server-content-negotiation:3.3.2")
            implementation("io.ktor:ktor-serialization-kotlinx-json:3.3.2")


            // For serialization if you use JSON messages
            implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0")
            implementation("io.ktor:ktor-client-content-negotiation:3.3.2")


            implementation("org.jmdns:jmdns:3.5.8")

            implementation(compose.materialIconsExtended)


        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.koin.test)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")

            // Required -- JUnit 4 framework
//    testImplementation("junit:junit:${jUnitVersion}")
// Optional -- Robolectric environment
//            implementation("androidx.test:core:1.7.0")
// Optional -- Mockito framework
            implementation("org.mockito:mockito-core:5.21.0")
// Optional -- mockito-kotlin
            implementation("org.mockito.kotlin:mockito-kotlin:6.2.3")
// Optional -- Mockk framework
            implementation("io.mockk:mockk:1.14.9")

            implementation("app.cash.turbine:turbine:1.2.1")

            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)


        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)

            implementation(libs.koin.core)

            implementation("io.ktor:ktor-client-java:3.3.2")
            implementation("io.ktor:ktor-client-cio:3.3.2")

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

    //    androidTestImplementation("androidx.compose.ui:ui-test-junit4-android:1.10.5")
    //    debugImplementation("androidx.compose.ui:ui-test-manifest:1.10.5")

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



