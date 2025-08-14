/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("android")
    id("com.android.application")
    kotlin("plugin.compose")
}


android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "demo.letsPlot"

    buildFeatures {
        compose = true
    }

    defaultConfig {
        applicationId = "demo.letsPlot.composeMinDemo"

        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()

        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("debug")
        }
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

val androidxActivityCompose = extra["androidx.activity.compose"] as String
val letsPlotVersion = extra["letsPlot.version"] as String
val letsPlotKotlinVersion = extra["letsPlotKotlin.version"] as String

dependencies {
    implementation(platform("androidx.compose:compose-bom:2023.08.00")) // Replace YYYY.MM.00 with the desired BOM version
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material")
    implementation("androidx.activity:activity-compose:$androidxActivityCompose")

    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-kernel:$letsPlotKotlinVersion")
    implementation("org.jetbrains.lets-plot:lets-plot-common:$letsPlotVersion")

    implementation("org.jetbrains.lets-plot:canvas:${letsPlotVersion}")
    implementation("org.jetbrains.lets-plot:plot-raster:${letsPlotVersion}")

    implementation(project(":lets-plot-compose"))
    implementation(project(":demo-plot-shared"))

    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("com.github.tony19:logback-android:3.0.0")
}
