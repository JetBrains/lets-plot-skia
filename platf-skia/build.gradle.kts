import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.util.*

/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    `maven-publish`
    signing
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

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }

    androidTarget {
        publishLibraryVariants("release")
    }

    sourceSets {
        commonMain {
            dependencies {
                compileOnly("org.jetbrains.skiko:skiko:$skikoVersion")

                compileOnly("org.jetbrains.lets-plot:commons:$letsPlotVersion")
                compileOnly("org.jetbrains.lets-plot:datamodel:$letsPlotVersion")
                compileOnly("org.jetbrains.lets-plot:plot-base:$letsPlotVersion")
                compileOnly("org.jetbrains.lets-plot:plot-stem:$letsPlotVersion")
                compileOnly("org.jetbrains.lets-plot:plot-builder:$letsPlotVersion")
            }
        }

        named("jvmMain") {
            dependencies {
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
                compileOnly("org.jetbrains.lets-plot:commons:$letsPlotVersion")
                compileOnly("org.jetbrains.lets-plot:datamodel:$letsPlotVersion")
                compileOnly("org.jetbrains.lets-plot:canvas:$letsPlotVersion")
                compileOnly("org.jetbrains.lets-plot:plot-base:$letsPlotVersion")
                compileOnly("org.jetbrains.lets-plot:plot-builder:$letsPlotVersion")
                compileOnly("org.jetbrains.lets-plot:plot-stem:$letsPlotVersion")
                compileOnly("org.jetbrains.lets-plot:plot-raster:$letsPlotVersion")
            }
        }

        named("androidInstrumentedTest") {
            dependencies {
                implementation(kotlin("test"))
                implementation("androidx.test.ext:junit:1.1.5")
                implementation("androidx.test.espresso:espresso-core:3.5.1")

                implementation("org.assertj:assertj-core:$assertjVersion")
                implementation("org.jetbrains.lets-plot:commons:$letsPlotVersion")
                implementation("org.jetbrains.lets-plot:datamodel:$letsPlotVersion")
                implementation("org.jetbrains.lets-plot:canvas:$letsPlotVersion")
                implementation("org.jetbrains.lets-plot:plot-base:$letsPlotVersion")
                implementation("org.jetbrains.lets-plot:plot-builder:$letsPlotVersion")
                implementation("org.jetbrains.lets-plot:plot-stem:$letsPlotVersion")
                implementation("org.jetbrains.lets-plot:plot-raster:$letsPlotVersion")
            }
        }
    }
}

android {
    namespace = "org.jetbrains.letsPlot.android.canvas"

    compileSdk = (findProperty("android.compileSdk") as String).toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions {

        //    animationsDisabled = true
    //    execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false // true - error: when compiling demo cant resolve classes
//            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
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

tasks.register("pullDebugImages") {
    doLast {
        val destDir = File(projectDir, "build/test-results/")
        destDir.mkdirs()

        // 1. Load local.properties
        val localProperties = Properties()
        val localPropertiesFile = File(rootProject.projectDir, "local.properties")
        if (localPropertiesFile.exists()) {
            FileInputStream(localPropertiesFile).use { fis ->
                localProperties.load(fis)
            }
        }

        // 2. Get sdk.dir from local.properties
        val sdkDir = localProperties.getProperty("sdk.dir") ?: System.getenv("ANDROID_HOME") ?: System.getProperty("android.home")
        if (sdkDir == null) {
            throw GradleException("sdk.dir not found in local.properties and ANDROID_HOME or android.home not set")
        }

        // 3. Construct adb executable path
        val adbPath = "$sdkDir/platform-tools/adb"
        val adbFile = File(adbPath)
        if (!adbFile.exists()) {
            throw GradleException("adb not found at $adbPath")
        }
        val adbExecutable = adbFile.absolutePath

        // Get the list of connected devices using adb devices
        val adbDevicesOutput = ByteArrayOutputStream()
        exec {
            commandLine(adbExecutable, "devices", "-l")
            standardOutput = adbDevicesOutput
            isIgnoreExitValue = true
        }

        val devicesOutput = adbDevicesOutput.toString()

        val devices = devicesOutput.reader().readLines()
            .drop(1) // Skip the header line
            .filter { it.isNotBlank() && !it.startsWith("* daemon") } // Remove empty lines
            .map { it.split("\\s+".toRegex())[0] }

        if (devices.isEmpty()) {
            println("No connected Android devices found.")
            return@doLast
        }

        devices.forEach { deviceSerial ->
            println("Pulling images from device: $deviceSerial")

            //The directory on device to pull from
            val devicePicturesDir = "/storage/emulated/0/Android/data/org.jetbrains.letsPlot.android.canvas.test/files/Pictures/"
            //The local directory to initially pull the images to
            val tempLocalDir = File(destDir, "temp_pictures")
            if (tempLocalDir.exists()) {
                tempLocalDir.deleteRecursively()
            }
            tempLocalDir.mkdirs()

            // Pull the images from the device to the temporary directory
            exec {
                commandLine(
                    adbExecutable,
                    "-s",
                    deviceSerial,
                    "pull",
                    devicePicturesDir,
                    tempLocalDir.absolutePath
                )
            }

            val tempPicturesDir = File(tempLocalDir, "Pictures")
            val diffImagesDir = File(destDir, "/diff_images/")
            if (diffImagesDir.exists()) {
                diffImagesDir.deleteRecursively()
            }
            diffImagesDir.mkdirs()

            //Move files from temporary dir to destination dir
            tempPicturesDir.listFiles()?.forEach { file ->
                file.copyTo(File(diffImagesDir, file.name))
            }
            //Delete temporary dir
            tempLocalDir.deleteRecursively()
        }
    }
}


///////////////////////////////////////////////
//  Publishing
///////////////////////////////////////////////

afterEvaluate {
    publishing {
        publications.forEach { pub ->
            with(pub as MavenPublication) {
                artifact(tasks.jarJavaDocs)

                pom {
                    name.set("Lets-Plot Skia Frontend")
                    description.set("Skia frontend for Lets-Plot multiplatform plotting library.")
                    url.set("https://github.com/JetBrains/lets-plot-skia")
                    licenses {
                        license {
                            name.set("MIT")
                            url.set("https://raw.githubusercontent.com/JetBrains/lets-plot-skia/master/LICENSE")
                        }
                    }
                    developers {
                        developer {
                            id.set("jetbrains")
                            name.set("JetBrains")
                            email.set("lets-plot@jetbrains.com")
                        }
                    }
                    scm {
                        url.set("https://github.com/JetBrains/lets-plot-skia")
                    }
                }
            }
        }

        repositories {
            mavenLocal {
                url = uri("$rootDir/.maven-publish-dev-repo")
            }
        }
    }
}

signing {
    if (!(project.version as String).contains("SNAPSHOT")) {
        sign(publishing.publications)
    }
}