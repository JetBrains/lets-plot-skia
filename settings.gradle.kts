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
        kotlin("android").version(kotlinVersion)
        kotlin("plugin.compose").version(kotlinVersion)

        id("org.jetbrains.compose").version(composeVersion)

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

//include("demo-svg-shared")
//include("demo-svg-android")
//include("demo-svg-compose-desktop")
//include("demo-svg-swing")

include("demo-plot-shared")
//include("demo-plot-compose-android-min")
//include("demo-plot-compose-android-median")
//include("demo-plot-compose-android-redraw")
//include("demo-plot-compose-desktop")
include("demo-plot-swing")

//project(":demo-svg-shared").projectDir = File("./demo/svg/shared")
//project(":demo-svg-android").projectDir = File("./demo/svg/android")
//project(":demo-svg-compose-desktop").projectDir = File("./demo/svg/compose-desktop")
//project(":demo-svg-swing").projectDir = File("./demo/svg/swing")

project(":demo-plot-shared").projectDir = File("./demo/plot/shared")
//project(":demo-plot-compose-android-min").projectDir = File("./demo/plot/compose-android-min")
//project(":demo-plot-compose-android-median").projectDir = File("./demo/plot/compose-android-median")
//project(":demo-plot-compose-android-redraw").projectDir = File("./demo/plot/compose-android-redraw")
//project(":demo-plot-compose-desktop").projectDir = File("./demo/plot/compose-desktop")
project(":demo-plot-swing").projectDir = File("./demo/plot/swing")
