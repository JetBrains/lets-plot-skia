/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
//    kotlin("multiplatform")
    kotlin("jvm")
}

val skikoVersion = extra["skiko.version"] as String
val letsPlotVersion = extra["letsPlot.version"] as String

//kotlin {
//    jvm {
//    }
//
//    js(IR) {
//        browser()
//        binaries.executable()
//    }
//
//    sourceSets {
//        val commonMain by getting {
//            dependencies {
//                compileOnly("org.jetbrains.skiko:skiko:$skiko_version")
//
//                compileOnly("org.jetbrains.lets-plot:base-portable:$lets_plot_version") { isTransitive = false }
//                compileOnly("org.jetbrains.lets-plot:base:$lets_plot_version") { isTransitive = false }
//                compileOnly("org.jetbrains.lets-plot:mapper-core:$lets_plot_version") { isTransitive = false }
//                compileOnly("org.jetbrains.lets-plot:vis-svg-portable:$lets_plot_version") { isTransitive = false }
//                compileOnly("org.jetbrains.lets-plot:vis-svg-mapper:$lets_plot_version") { isTransitive = false }
//            }
//        }
//
//        val commonTest by getting {
//            dependencies {
//                implementation(kotlin("test"))
//            }
//        }
//
//        val jvmMain by getting {
//            dependencies {
//                implementation("io.github.microutils:kotlin-logging-jvm:2.0.5") // TODO remove with other { isTransitive = false }
//            }
//        }
//
//        val jsMain by getting {
//            dependencies {
//                implementation("io.github.microutils:kotlin-logging-js:2.0.5") // TODO remove with other { isTransitive = false }
//            }
//        }
//    }
//}

dependencies {
    compileOnly("org.jetbrains.skiko:skiko:$skikoVersion")

    compileOnly("org.jetbrains.lets-plot:base-portable:$letsPlotVersion") { isTransitive = false }
    compileOnly("org.jetbrains.lets-plot:base:$letsPlotVersion") { isTransitive = false }
    compileOnly("org.jetbrains.lets-plot:mapper-core:$letsPlotVersion") { isTransitive = false }
    compileOnly("org.jetbrains.lets-plot:vis-svg-portable:$letsPlotVersion") { isTransitive = false }
    compileOnly("org.jetbrains.lets-plot:vis-svg-mapper:$letsPlotVersion") { isTransitive = false }

    testImplementation(kotlin("test"))
}