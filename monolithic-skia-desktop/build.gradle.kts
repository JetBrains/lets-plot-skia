/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("jvm")
}

val lets_plot_version: String by extra
val skiko_version: String by extra

dependencies {
    compileOnly("org.jetbrains.skiko:skiko:$skiko_version")
    compileOnly(project(":svg-mapper-skia"))

    // Property, SimpleComposite
    compileOnly("org.jetbrains.lets-plot:base-portable:$lets_plot_version") { isTransitive = false }

    // Mapper, MappingContext
    compileOnly("org.jetbrains.lets-plot:mapper-core:$lets_plot_version") { isTransitive = false }

    // SvgSvgElement, SvgNodeContainer, SvgGElement, SvgTextElement, etc
    compileOnly("org.jetbrains.lets-plot:vis-svg-portable:$lets_plot_version") { isTransitive = false }

    // FigureBuildInfo, CompositeFigureSvgRoot, PlotSvgRoot, PlotAssembler, PlotSvgComponent, PlotContainerPortable, MouseEventPeer
    compileOnly("org.jetbrains.lets-plot:plot-builder-portable:$lets_plot_version") { isTransitive = false }

    // PlotContainer
    compileOnly("org.jetbrains.lets-plot:plot-builder:$lets_plot_version") { isTransitive = false }

    // MonolithicCommon, PlotConfig, PlotsBuildResult
    compileOnly("org.jetbrains.lets-plot:plot-config-portable:$lets_plot_version") { isTransitive = false }

    // > Task :monolithic-skia:compileKotlinJs FAILED
    // java.lang.IllegalStateException: FATAL ERROR: Could not find "org.jetbrains.lets-plot:xxx" in [/Users/ikupriyanov/Library/Application Support/kotlin/daemon]
    //        at org.jetbrains.kotlin.ir.backend.js.KlibKt$toResolverLogger$1.fatal(klib.kt:110)
    compileOnly("org.jetbrains.lets-plot:base:$lets_plot_version") { isTransitive = false }
    compileOnly("org.jetbrains.lets-plot:vis-svg-mapper:$lets_plot_version") { isTransitive = false }
    compileOnly("org.jetbrains.lets-plot:plot-base-portable:$lets_plot_version") { isTransitive = false }
    compileOnly("org.jetbrains.lets-plot:plot-common-portable:$lets_plot_version") { isTransitive = false }
    compileOnly("org.jetbrains.lets-plot:vis-canvas:$lets_plot_version") { isTransitive = false }

    implementation("io.github.microutils:kotlin-logging-jvm:2.0.5") // TODO remove with other { isTransitive = false }

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
