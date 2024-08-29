import com.android.build.gradle.tasks.MergeSourceSetFolders
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("android")
    id("com.android.application")
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

    implementation(project(":platf-skia"))

    implementation(project(":demo-svg-shared"))

    implementation("org.jetbrains.lets-plot:lets-plot-common:$letsPlotVersion")
}
