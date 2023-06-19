/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

val skikoVersion = extra["skiko.version"] as String
val letsPlotVersion = extra["letsPlot.version"] as String

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }

    android()


    sourceSets {
        val commonMain by getting {
            dependencies {
                compileOnly("org.jetbrains.skiko:skiko:$skikoVersion")

                implementation("org.jetbrains.lets-plot:base:$letsPlotVersion") { isTransitive = false }
                implementation("org.jetbrains.lets-plot:base-portable:$letsPlotVersion") { isTransitive = false }
                implementation("org.jetbrains.lets-plot:mapper-core:$letsPlotVersion") { isTransitive = false }
                implementation("org.jetbrains.lets-plot:vis-svg-portable:$letsPlotVersion") { isTransitive = false }
                implementation("org.jetbrains.lets-plot:vis-svg-mapper:$letsPlotVersion") { isTransitive = false }
            }
        }

        val jvmMain by getting {
            dependencies {
//                compileOnly("org.jetbrains.skiko:skiko-awt:$skikoVersion")

                implementation("io.github.microutils:kotlin-logging-jvm:2.0.5") // TODO remove with other { isTransitive = false }
            }
        }

        val androidMain by getting {
            dependencies {
                compileOnly("org.jetbrains.skiko:skiko-android:$skikoVersion")
            }
        }
    }
}

android {
    namespace = "org.jetbrains.letsPlot.skia.mapper"

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
