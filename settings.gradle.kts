pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
//        mavenCentral()
    }

    plugins {
        val kotlinVersion = extra["kotlin.version"] as String
        val composePluginVersion = extra["composePlugin.version"] as String
        val agpVersion = extra["agp.version"] as String

        kotlin("jvm").version(kotlinVersion)
        kotlin("multiplatform").version(kotlinVersion)
        kotlin("android").version(kotlinVersion)

        id("org.jetbrains.compose").version(composePluginVersion)

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

include("demo-svg-mapping-shared")
include("demo-svg-mapping-android")
include("demo-svg-mapping-compose")
include("demo-svg-mapping-swing")

include("demo-plot-shared")
include("demo-plot-compose-android-min")
include("demo-plot-compose-android-median")
include("demo-plot-compose-desktop")
include("demo-plot-swing")

project(":demo-svg-mapping-shared").projectDir = File("./demo/svg-mapping/shared")
project(":demo-svg-mapping-android").projectDir = File("./demo/svg-mapping/android")
project(":demo-svg-mapping-compose").projectDir = File("./demo/svg-mapping/compose")
project(":demo-svg-mapping-swing").projectDir = File("./demo/svg-mapping/swing")

project(":demo-plot-shared").projectDir = File("./demo/plot/shared")
project(":demo-plot-compose-android-min").projectDir = File("./demo/plot/compose-android-min")
project(":demo-plot-compose-android-median").projectDir = File("./demo/plot/compose-android-median")
project(":demo-plot-compose-desktop").projectDir = File("./demo/plot/compose-desktop")
project(":demo-plot-swing").projectDir = File("./demo/plot/swing")
