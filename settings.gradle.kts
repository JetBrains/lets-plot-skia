pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        val kotlinVersion = extra["kotlin.version"] as String
        val composeVersion = extra["compose.version"] as String
        val agpVersion = extra["agp.version"] as String
        val nexusStagingVersion = extra["nexusStaging.version"] as String
        val nexusPublishVersion = extra["nexusPublish.version"] as String

        kotlin("jvm").version(kotlinVersion)
        kotlin("multiplatform").version(kotlinVersion)
        kotlin("plugin.compose").version(kotlinVersion)
        id("org.jetbrains.compose").version(composeVersion)

        kotlin("android").version(kotlinVersion)
        id("com.android.application").version(agpVersion)
        id("com.android.library").version(agpVersion)

        id("io.codearte.nexus-staging") version nexusStagingVersion
        id("io.github.gradle-nexus.publish-plugin") version nexusPublishVersion
    }
}

include("platf-skia")
include("platf-skia-awt")
include("lets-plot-compose")
include("lets-plot-swing-skia")

//----- Demos -----

// SVG Shared
include("demo-svg-shared")
project(":demo-svg-shared").projectDir = File("./demo/svg/shared")


// Plot Shared
include("demo-plot-shared")
project(":demo-plot-shared").projectDir = File("./demo/plot/shared")


// Compose SVG
include("demo-svg-compose-desktop")
project(":demo-svg-compose-desktop").projectDir = File("./demo/svg/compose-desktop")


// Compose Plot
include("demo-plot-compose-desktop")
project(":demo-plot-compose-desktop").projectDir = File("./demo/plot/compose-desktop")


// SWING SVG
include("demo-svg-swing")
project(":demo-svg-swing").projectDir = File("./demo/svg/swing")


// SWING Plot
include("demo-plot-swing")
project(":demo-plot-swing").projectDir = File("./demo/plot/swing")

// Android SVG
include("demo-svg-android")
project(":demo-svg-android").projectDir = File("./demo/svg/android")


/*
// Android Plot Min
include("demo-plot-compose-android-min")
project(":demo-plot-compose-android-min").projectDir = File("./demo/plot/compose-android-min")


// Android Plot Median
include("demo-plot-compose-android-median")
project(":demo-plot-compose-android-median").projectDir = File("./demo/plot/compose-android-median")


// Android Plot Redraw
include("demo-plot-compose-android-redraw")
project(":demo-plot-compose-android-redraw").projectDir = File("./demo/plot/compose-android-redraw")
*/