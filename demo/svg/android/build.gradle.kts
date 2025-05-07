/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import com.android.build.gradle.tasks.MergeSourceSetFolders
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("android")
    id("com.android.application")
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "demo.svgMapping.SvgSkiaMappingDemo"

    defaultConfig {
        applicationId = "demo.svgMapping.SvgSkiaMappingDemo"

        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()

        versionCode = 1
        versionName = "1.0"

        ndk {
            abiFilters += listOf("x86_64", "arm64-v8a")
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        jvmToolchain(11)
    }
}

val letsPlotVersion = extra["letsPlot.version"] as String

dependencies {
    implementation(project(":platf-skia"))
    implementation(project(":demo-svg-shared"))
    implementation("org.jetbrains.lets-plot:lets-plot-common:$letsPlotVersion")
    implementation("org.jetbrains.lets-plot:canvas:${letsPlotVersion}")
    implementation("org.jetbrains.lets-plot:plot-raster:$letsPlotVersion")
}
