plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    kotlin("multiplatform").version("2.0.0").apply(false)
    kotlin("android").version("2.0.0").apply(false)
    id("org.jetbrains.kotlin.plugin.compose").version("2.0.0").apply(false)
    id("com.android.application").version("8.6.0").apply(false)
    id("com.android.library").version("8.6.0").apply(false)
    id("org.jetbrains.compose").version("1.6.11").apply(false)
    id("com.google.gms.google-services") version "4.4.4" apply false
    id("com.apollographql.apollo3") version "3.8.2" apply false
}


