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

        id("org.jetbrains.compose").version(composeVersion)

        id("com.android.application").version(agpVersion)
        id("com.android.library").version(agpVersion)
    }
}

//dependencyResolutionManagement {
//    repositories {
//        google()
//        mavenCentral()
//        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
//    }
//}


include("skia-svg-mapper")
include("plot-compose")
include("plot-swing")
include("skia-awt")
include("skia-android")

include("demo-svg-shared")
include("demo-svg-android")
include("demo-svg-compose-desktop")
include("demo-svg-swing")

include("demo-plot-shared")
include("demo-plot-compose-android-min")
include("demo-plot-compose-android-median")
include("demo-plot-compose-desktop")
include("demo-plot-swing")

project(":demo-svg-shared").projectDir = File("./demo/svg/shared")
project(":demo-svg-android").projectDir = File("./demo/svg/android")
project(":demo-svg-compose-desktop").projectDir = File("./demo/svg/compose-desktop")
project(":demo-svg-swing").projectDir = File("./demo/svg/swing")

project(":demo-plot-shared").projectDir = File("./demo/plot/shared")
project(":demo-plot-compose-android-min").projectDir = File("./demo/plot/compose-android-min")
project(":demo-plot-compose-android-median").projectDir = File("./demo/plot/compose-android-median")
project(":demo-plot-compose-desktop").projectDir = File("./demo/plot/compose-desktop")
project(":demo-plot-swing").projectDir = File("./demo/plot/swing")
