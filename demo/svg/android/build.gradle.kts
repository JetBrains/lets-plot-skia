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

//////////////////////////////////////////////////////////////////////////////////////////
// Android + Skiko boilerplate
// from Skia Android Sample:
// https://github.com/JetBrains/skiko/blob/master/samples/SkiaAndroidSample/build.gradle.kts
//////////////////////////////////////////////////////////////////////////////////////////

val skikoNativeX64: Configuration by configurations.creating
val skikoNativeArm64: Configuration by configurations.creating

val jniDir = "${projectDir.absolutePath}/src/main/jniLibs"

val unzipTaskX64 = tasks.register("unzipNativeX64", Copy::class) {
    destinationDir = file("$jniDir/x86_64")
    from(skikoNativeX64.map { zipTree(it) }) {
        include("*.so")
    }
    includeEmptyDirs = false
}

val unzipTaskArm64 = tasks.register("unzipNativeArm64", Copy::class) {
    destinationDir = file("$jniDir/arm64-v8a")
    from(skikoNativeArm64.map { zipTree(it) }) {
        include("*.so")
    }
    includeEmptyDirs = false
}

tasks.withType<MergeSourceSetFolders>().configureEach {
    dependsOn(unzipTaskX64)
    dependsOn(unzipTaskArm64)
}

tasks.withType<KotlinJvmCompile>().configureEach {
    dependsOn(unzipTaskX64)
    dependsOn(unzipTaskArm64)
}
//////////////////////////////////////////////////////////////////////////////////////////

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

val skikoVersion = extra["skiko.version"] as String
val letsPlotVersion = extra["letsPlot.version"] as String

dependencies {
    implementation("org.jetbrains.skiko:skiko-android:$skikoVersion")

    skikoNativeX64("org.jetbrains.skiko:skiko-android-runtime-x64:$skikoVersion")
    skikoNativeArm64("org.jetbrains.skiko:skiko-android-runtime-arm64:$skikoVersion")

    implementation(project(":skia-svg-mapper"))
    implementation(project(":skia-android"))

    implementation(project(":demo-svg-shared"))

    implementation("org.jetbrains.lets-plot:lets-plot-common:$letsPlotVersion")
}
