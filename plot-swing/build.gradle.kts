/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("jvm")
}

val skikoVersion = extra["skiko.version"] as String
val letsPlotVersion = extra["letsPlot.version"] as String
val letsPlotKotlinVersion = extra["letsPlotKotlin.version"] as String

dependencies {
    compileOnly("org.jetbrains.skiko:skiko:$skikoVersion")

    compileOnly("org.jetbrains.lets-plot:lets-plot-kotlin-kernel:$letsPlotKotlinVersion")

    implementation(project(":skia-svg-mapper"))
    implementation(project(":skia-awt"))

    // PortableLogging
    implementation("org.jetbrains.lets-plot:base-portable:$letsPlotVersion") { isTransitive = false }

    // SvgSvgElement, SvgNodeContainer, SvgGElement, SvgTextElement, etc
    implementation("org.jetbrains.lets-plot:vis-svg-portable:$letsPlotVersion") { isTransitive = false }

    // MonolithicCommon, PlotConfig, PlotsBuildResult
    implementation("org.jetbrains.lets-plot:plot-config-portable:$letsPlotVersion") { isTransitive = false }

    // jetbrains.datalore.vis.swing.PlotSpecComponentProvider (For Skia in Swing app), PlotPanel
    api("org.jetbrains.lets-plot:vis-swing-common:$letsPlotVersion") { isTransitive = false }

//    implementation("io.github.microutils:kotlin-logging-jvm:2.0.5") // TODO remove with other { isTransitive = false }

    testImplementation(kotlin("test"))
}

//android {
//    compileSdk = 31
//    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
//    defaultConfig {
//        minSdk = 21
//        targetSdk = 31
//    }
//}
