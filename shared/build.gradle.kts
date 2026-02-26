import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization").version("2.0.0")
    id("com.apollographql.apollo3")
}


kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    
    // Only enable iOS targets on macOS to avoid tooling issues on Linux/Windows
    if (System.getProperty("os.name").startsWith("Mac")) {
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
    }

    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            implementation("io.ktor:ktor-client-core:2.3.8")
            implementation("com.apollographql.apollo3:apollo-runtime:3.8.2")
            implementation("com.apollographql.apollo3:apollo-normalized-cache:3.8.2")
            implementation("com.apollographql.apollo3:apollo-normalized-cache-sqlite:3.8.2")
            implementation("io.ktor:ktor-client-content-negotiation:2.3.8")
            implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.8")
            implementation("io.ktor:ktor-client-logging:2.3.8")
            implementation("io.insert-koin:koin-core:3.5.3")
            implementation("io.insert-koin:koin-compose:1.1.2")
            implementation("io.insert-koin:koin-compose-viewmodel:1.2.0-Beta4")
            implementation("com.russhwolf:multiplatform-settings-no-arg:1.1.1")
            
            // Compose Multiplatform
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.0")
        }
        androidMain.dependencies {
            implementation("io.ktor:ktor-client-okhttp:2.3.8")
            // Added for Android Studio Preview support
            implementation(compose.uiTooling)
        }
        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:2.3.8")
        }
    }
}

android {
    namespace = "com.bali.shared"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    flavorDimensions.add("environment")
    productFlavors {
        create("dev") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"http://192.168.68.130:8080/graphql\"")
        }
        create("prod") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"https://bali-backend-production.up.railway.app/graphql\"")
        }
    }
    buildFeatures {
        buildConfig = true
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

apollo {
    service("service") {
        packageName.set("com.bali.graphql")
        srcDir("src/commonMain/graphql/com/bali")
    }
}
