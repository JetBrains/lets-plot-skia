/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

val skikoVersion = extra["skiko.version"] as String
val letsPlotVersion = extra["letsPlot.version"] as String

repositories {
    google()
    mavenCentral()
    mavenLocal()
}

kotlin {
    //jvm()
    linuxX64 {
        binaries {
            executable {
                // TODO: libskia.o is required on Linux, but not on MacOS.
                //linkerOpts += listOf("-L/usr/lib/x86_64-linux-gnu/", "-lfontconfig", "-L"+"/home/ikupriyanov/Downloads/default/targets/linux_x64/included", "-lskia", "--allow-shlib-undefined")
                linkerOpts += listOf("-L/usr/lib/x86_64-linux-gnu/", "-lfontconfig")
            }
        }
    }

    //macosArm64 {
    //    binaries {
    //        executable()
    //    }
    //}

    sourceSets {
        all {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
            dependencies {
                implementation("com.squareup.okio:okio:3.9.0")
                implementation("org.jetbrains.skiko:skiko:$skikoVersion")
                implementation("org.jetbrains.lets-plot:commons:$letsPlotVersion")
                implementation("org.jetbrains.lets-plot:datamodel:$letsPlotVersion")
                implementation("org.jetbrains.lets-plot:plot-base:$letsPlotVersion")
                implementation("org.jetbrains.lets-plot:plot-builder:$letsPlotVersion")
                implementation("org.jetbrains.lets-plot:plot-stem:$letsPlotVersion")
                implementation("org.jetbrains.lets-plot:canvas:$letsPlotVersion")
                implementation("io.github.microutils:kotlin-logging:2.0.6")
                implementation(project(":lets-plot-raster"))
            }
        }
    }
}
