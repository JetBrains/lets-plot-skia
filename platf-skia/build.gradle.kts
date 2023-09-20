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
val kotlinLoggingVersion = extra["kotlinLogging.version"] as String
val assertjVersion = extra["assertj.version"] as String

val osName = System.getProperty("os.name")!!
val hostOs = when {
    osName == "Mac OS X" -> "macos"
    osName.startsWith("Win") -> "windows"
    osName.startsWith("Linux") -> "linux"
    else -> error("Unsupported OS: $osName")
}

var hostArch = when (val osArch = System.getProperty("os.arch")) {
    "x86_64", "amd64" -> "x64"
    "aarch64" -> "arm64"
    else -> error("Unsupported arch: $osArch")
}

val host = "${hostOs}-${hostArch}"

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }

    androidTarget()

    sourceSets {
        commonMain {
            dependencies {
                compileOnly("org.jetbrains.skiko:skiko:$skikoVersion")

                compileOnly("org.jetbrains.lets-plot:commons:$letsPlotVersion")
                compileOnly("org.jetbrains.lets-plot:datamodel:$letsPlotVersion")

                compileOnly("org.jetbrains.lets-plot:deprecated-in-v4:$letsPlotVersion")
            }
        }

        named("jvmMain") {
            dependencies {
//                compileOnly("org.jetbrains.skiko:skiko-awt:$skikoVersion")

                compileOnly("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
            }
        }

        named("jvmTest") {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.assertj:assertj-core:$assertjVersion")
                implementation("org.jetbrains.skiko:skiko:$skikoVersion")
                implementation("org.jetbrains.skiko:skiko-awt-runtime-$hostOs-$hostArch:$skikoVersion")
                implementation("org.jetbrains.lets-plot:commons:$letsPlotVersion")
                implementation("org.jetbrains.lets-plot:datamodel:$letsPlotVersion")
                implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
            }
        }

        named("androidMain") {
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
