pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
//        mavenCentral()
    }

    plugins {
        val kotlinVersion = extra["kotlin.version"] as String
        val composeVersion = extra["compose.version"] as String
        val agpVersion = extra["agp.version"] as String

        kotlin("jvm").version(kotlinVersion)
        kotlin("multiplatform").version(kotlinVersion)
        kotlin("android").version(kotlinVersion)

//        id("com.android.base").version(agpVersion)
        id("com.android.application").version(agpVersion)
        id("com.android.library").version(agpVersion)

        id("org.jetbrains.compose").version(composeVersion)
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}


include("svg-mapper-skia")
include("demo-swing-app")
include("demo-android-app")
include("demo-compose-app")
include("monolithic-skia-desktop")
include("monolithic-skia-android")

include("demo-svg-mapping-shared")
include("demo-svg-mapping-android")
include("demo-svg-mapping-compose")
include("demo-svg-mapping-swing")

project(":demo-svg-mapping-shared").projectDir = File("./demo/svg-mapping/shared")
project(":demo-svg-mapping-android").projectDir = File("./demo/svg-mapping/android")
project(":demo-svg-mapping-compose").projectDir = File("./demo/svg-mapping/compose")
project(":demo-svg-mapping-swing").projectDir = File("./demo/svg-mapping/swing")
