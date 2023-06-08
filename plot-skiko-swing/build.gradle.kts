/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("jvm")
}

val skikoVersion = extra["skiko.version"] as String
val letsPlotVersion = extra["letsPlot.version"] as String

dependencies {
    compileOnly("org.jetbrains.skiko:skiko:$skikoVersion")
//    compileOnly(project(":svg-mapper-skia"))

    // PortableLogging
    compileOnly("org.jetbrains.lets-plot:base-portable:$letsPlotVersion") { isTransitive = false }

    // Mapper, MappingContext
//    compileOnly("org.jetbrains.lets-plot:mapper-core:$letsPlotVersion") { isTransitive = false }

    // SvgSvgElement, SvgNodeContainer, SvgGElement, SvgTextElement, etc
    compileOnly("org.jetbrains.lets-plot:vis-svg-portable:$letsPlotVersion") { isTransitive = false }

    // FigureBuildInfo, CompositeFigureSvgRoot, PlotSvgRoot, PlotAssembler, PlotSvgComponent, PlotContainerPortable, MouseEventPeer
    compileOnly("org.jetbrains.lets-plot:plot-builder-portable:$letsPlotVersion") { isTransitive = false }

    // PlotContainer
    compileOnly("org.jetbrains.lets-plot:plot-builder:$letsPlotVersion") { isTransitive = false }

    // MonolithicCommon, PlotConfig, PlotsBuildResult
    compileOnly("org.jetbrains.lets-plot:plot-config-portable:$letsPlotVersion") { isTransitive = false }

    // > Task :monolithic-skia:compileKotlinJs FAILED
    // java.lang.IllegalStateException: FATAL ERROR: Could not find "org.jetbrains.lets-plot:xxx" in [/Users/ikupriyanov/Library/Application Support/kotlin/daemon]
    //        at org.jetbrains.kotlin.ir.backend.js.KlibKt$toResolverLogger$1.fatal(klib.kt:110)
    compileOnly("org.jetbrains.lets-plot:base:$letsPlotVersion") { isTransitive = false }
//    compileOnly("org.jetbrains.lets-plot:vis-svg-mapper:$letsPlotVersion") { isTransitive = false }
    compileOnly("org.jetbrains.lets-plot:plot-base-portable:$letsPlotVersion") { isTransitive = false }
    compileOnly("org.jetbrains.lets-plot:plot-common-portable:$letsPlotVersion") { isTransitive = false }
//    compileOnly("org.jetbrains.lets-plot:vis-canvas:$letsPlotVersion") { isTransitive = false }

    // jetbrains.datalore.vis.swing.PlotSpecComponentProvider (For Skia in Swing app), PlotPanel
    api("org.jetbrains.lets-plot:vis-swing-common:$letsPlotVersion") { isTransitive = false }

//    implementation("io.github.microutils:kotlin-logging-jvm:2.0.5") // TODO remove with other { isTransitive = false }

    implementation(project(":monolithic-skia-desktop"))

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