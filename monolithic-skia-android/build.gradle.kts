/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
// Merely "android" project doesn't work because in some ways it is
// treated as a "jvm" project and links to Skiko Swing artifacts.
// As Swing classes are not available on Android, such configuration fails with errors like:
// "Cannot access 'java.awt.Component' which is a supertype of 'org.jetbrains.skiko.SkiaLayer'. Check your module classpath for missing or conflicting dependencies"

//    kotlin("android")

    kotlin("multiplatform")
    id("com.android.library")
}

val skikoVersion = extra["skiko.version"] as String
val letsPlotVersion = extra["letsPlot.version"] as String

kotlin {
    android()

    sourceSets {
        val commonMain by getting {
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("org.jetbrains.skiko:skiko-android:$skikoVersion")

                compileOnly(project(":svg-mapper-skia"))

                // Property, SimpleComposite
                compileOnly("org.jetbrains.lets-plot:base-portable:$letsPlotVersion") { isTransitive = false }

                // Mapper, MappingContext
                compileOnly("org.jetbrains.lets-plot:mapper-core:$letsPlotVersion") { isTransitive = false }

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
                compileOnly("org.jetbrains.lets-plot:vis-svg-mapper:$letsPlotVersion") { isTransitive = false }
                compileOnly("org.jetbrains.lets-plot:plot-base-portable:$letsPlotVersion") { isTransitive = false }
                compileOnly("org.jetbrains.lets-plot:plot-common-portable:$letsPlotVersion") { isTransitive = false }
                compileOnly("org.jetbrains.lets-plot:vis-canvas:$letsPlotVersion") { isTransitive = false }
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
    }
}
