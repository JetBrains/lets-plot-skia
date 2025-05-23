/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import com.android.build.gradle.tasks.MergeSourceSetFolders
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("android")
    id("org.jetbrains.compose")
    id("com.android.application")
    kotlin("plugin.compose")
}

val skikoNativeX64: Configuration by configurations.creating
val skikoNativeArm64: Configuration by configurations.creating

//////////////////////////////////////////////////////////////////////////////////////////

val copyJniLibs = tasks.register("copyJniLibs", Copy::class) {
    val srcJniLibsDir = "${project.rootProject.projectDir}/skiko-jni-libs/"
    val dstJniLibsDir = "${project.projectDir}/src/main/jniLibs/"

    from(srcJniLibsDir)
    into(dstJniLibsDir)
    include("**/*")
}

tasks.withType<MergeSourceSetFolders>().configureEach {
    dependsOn(copyJniLibs)
}

tasks.withType<KotlinJvmCompile>().configureEach {
    dependsOn(copyJniLibs)
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

        ndk {
            abiFilters += listOf("x86_64", "arm64-v8a")
        }
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

val composeVersion = extra["compose.version"] as String
val androidxActivityCompose = extra["androidx.activity.compose"] as String
val skikoVersion = extra["skiko.version"] as String
val letsPlotVersion = extra["letsPlot.version"] as String
val letsPlotKotlinVersion = extra["letsPlotKotlin.version"] as String

dependencies {
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material)
    implementation(compose.ui)
    implementation("androidx.activity:activity-compose:$androidxActivityCompose")

    implementation("org.jetbrains.skiko:skiko-android:$skikoVersion")

    skikoNativeX64("org.jetbrains.skiko:skiko-android-runtime-x64:$skikoVersion")
    skikoNativeArm64("org.jetbrains.skiko:skiko-android-runtime-arm64:$skikoVersion")

    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-kernel:$letsPlotKotlinVersion")
    implementation("org.jetbrains.lets-plot:lets-plot-common:$letsPlotVersion")

    implementation(project(":lets-plot-compose"))
    implementation(project(":demo-plot-shared"))

    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("com.github.tony19:logback-android:3.0.0")
}

