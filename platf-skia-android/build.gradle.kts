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
    androidTarget()

    sourceSets {
        named("commonMain") {
        }

        named("commonTest") {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        named("androidMain") {
            dependencies {
                compileOnly("org.jetbrains.skiko:skiko-android:$skikoVersion")

                compileOnly(project(":platf-skia"))

                compileOnly("org.jetbrains.lets-plot:commons:$letsPlotVersion")
                compileOnly("org.jetbrains.lets-plot:datamodel:$letsPlotVersion")
                compileOnly("org.jetbrains.lets-plot:plot-base:$letsPlotVersion")
                compileOnly("org.jetbrains.lets-plot:plot-builder:$letsPlotVersion")
                compileOnly("org.jetbrains.lets-plot:plot-stem:$letsPlotVersion")
            }
        }
    }
}

android {
    namespace = "org.jetbrains.letsPlot.skiko.android"

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
