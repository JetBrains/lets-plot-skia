/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
}

val skikoVersion = extra["skiko.version"] as String
val letsPlotVersion = extra["letsPlot.version"] as String
val letsPlotKotlinVersion = extra["letsPlotKotlin.version"] as String

kotlin {
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }

    android()


    sourceSets {
        named("commonMain") {
            dependencies {
//                implementation(compose.desktop.currentOs)
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.ui)


                compileOnly("org.jetbrains.skiko:skiko:$skikoVersion")

                api("org.jetbrains.lets-plot:lets-plot-kotlin-kernel:$letsPlotKotlinVersion") //{ isTransitive = false }

                compileOnly("org.jetbrains.lets-plot:base-portable:$letsPlotVersion") { isTransitive = false }
//                compileOnly("org.jetbrains.lets-plot:base:$letsPlotVersion") { isTransitive = false }
//                compileOnly("org.jetbrains.lets-plot:mapper-core:$letsPlotVersion") { isTransitive = false }
//                compileOnly("org.jetbrains.lets-plot:vis-svg-portable:$letsPlotVersion") { isTransitive = false }
//                compileOnly("org.jetbrains.lets-plot:vis-svg-mapper:$letsPlotVersion") { isTransitive = false }

//                implementation("io.github.microutils:kotlin-logging-jvm:2.0.5") // TODO remove with other { isTransitive = false }
                implementation("io.github.microutils:kotlin-logging:2.0.5") // TODO remove with other { isTransitive = false }
            }
        }

        named("desktopMain") {
            dependencies {
                implementation(compose.desktop.currentOs)
//                compileOnly("org.jetbrains.skiko:skiko-awt:$skikoVersion")
                api(project(":skia-awt"))

                implementation("io.github.microutils:kotlin-logging-jvm:2.0.5") // TODO remove with other { isTransitive = false }
            }
        }

        named("androidMain") {
            dependencies {
                compileOnly("org.jetbrains.skiko:skiko-android:$skikoVersion") {
//                    exclude("org.jetbrains.skiko", "skiko-awt")
                }
                api(project(":skia-android"))
            }
        }
    }
}

android {
    namespace = "org.jetbrains.letsPlot.skia.compose"

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
