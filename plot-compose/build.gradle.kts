/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
}

val skikoVersion = extra["skiko.version"] as String
val composeVersion = extra["compose.version"] as String
val letsPlotVersion = extra["letsPlot.version"] as String
val letsPlotKotlinVersion = extra["letsPlotKotlin.version"] as String
val kotlinLoggingVersion = extra["kotlinLogging.version"] as String

kotlin {
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }

    android()


    sourceSets {
        named("commonMain") {
            dependencies {
                compileOnly(compose.runtime)
                compileOnly(compose.ui)

                compileOnly("org.jetbrains.skiko:skiko:$skikoVersion")
                compileOnly("org.jetbrains.lets-plot:lets-plot-kotlin-kernel:$letsPlotKotlinVersion")

                compileOnly("org.jetbrains.lets-plot:commons:$letsPlotVersion")
                compileOnly("org.jetbrains.lets-plot:datamodel:$letsPlotVersion")
                compileOnly("org.jetbrains.lets-plot:plot-base:$letsPlotVersion")
                compileOnly("org.jetbrains.lets-plot:plot-builder:$letsPlotVersion")
                compileOnly("org.jetbrains.lets-plot:plot-stem:$letsPlotVersion")

                compileOnly("org.jetbrains.lets-plot:deprecated-in-v4:$letsPlotVersion")
            }
        }

        named("desktopMain") {
            dependencies {
                implementation(compose.desktop.currentOs)
//                compileOnly("org.jetbrains.skiko:skiko-awt:$skikoVersion")
                api(project(":skia-awt"))

                compileOnly("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
            }
        }

        named("androidMain") {
            dependencies {
                compileOnly("org.jetbrains.skiko:skiko-android:$skikoVersion")

                api(project(":skia-android"))
            }
        }
    }
}

android {
    namespace = "org.jetbrains.letsPlot.skia.compose"

    compileSdk = (findProperty("android.compileSdk") as String).toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        jvmToolchain(11)
    }
}
