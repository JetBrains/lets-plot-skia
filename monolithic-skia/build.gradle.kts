/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */
plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

val lets_plot_version: String by extra
val skiko_version: String by extra

kotlin {
    jvm {
    }

    js(IR) {
        browser()
        binaries.executable()
    }

    android()

    sourceSets {
        val commonMain by getting {
            dependencies {
                compileOnly("org.jetbrains.skiko:skiko:$skiko_version")
                implementation(project(":svg-mapper-skia"))

                compileOnly("org.jetbrains.lets-plot:base-portable:$lets_plot_version") { isTransitive = false }
                compileOnly("org.jetbrains.lets-plot:base:$lets_plot_version") { isTransitive = false }
                compileOnly("org.jetbrains.lets-plot:mapper-core:$lets_plot_version") { isTransitive = false }
                compileOnly("org.jetbrains.lets-plot:vis-svg-portable:$lets_plot_version") { isTransitive = false }
                compileOnly("org.jetbrains.lets-plot:vis-svg-mapper:$lets_plot_version") { isTransitive = false }
                compileOnly("org.jetbrains.lets-plot:vis-canvas:$lets_plot_version") { isTransitive = false }
                compileOnly("org.jetbrains.lets-plot:plot-common-portable:$lets_plot_version") { isTransitive = false }
                compileOnly("org.jetbrains.lets-plot:plot-base-portable:$lets_plot_version") { isTransitive = false }
                // PlotAssembler, PlotSvgComponent, PlotContainerPortable, MouseEventPeer
                compileOnly("org.jetbrains.lets-plot:plot-builder-portable:$lets_plot_version") { isTransitive = false }
                // PlotContainer
                compileOnly("org.jetbrains.lets-plot:plot-builder:$lets_plot_version") { isTransitive = false }
                // MonolithicCommon, PlotConfig, PlotsBuildResult
                compileOnly("org.jetbrains.lets-plot:plot-config-portable:$lets_plot_version") { isTransitive = false }
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("io.github.microutils:kotlin-logging-jvm:2.0.5") // TODO remove with other { isTransitive = false }
                implementation("org.jetbrains.lets-plot:base-portable:$lets_plot_version") { isTransitive = false }
                implementation("org.jetbrains.lets-plot:plot-builder-portable:$lets_plot_version") { isTransitive = false }
                implementation("org.jetbrains.lets-plot:plot-config-portable:$lets_plot_version") { isTransitive = false }
                implementation("org.jetbrains.lets-plot:vis-svg-portable:$lets_plot_version") { isTransitive = false }
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("org.jetbrains.skiko:skiko-android:$skiko_version")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation("io.github.microutils:kotlin-logging-js:2.0.5") // TODO remove with other { isTransitive = false }
            }
        }
    }
}

android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }
}
