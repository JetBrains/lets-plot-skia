/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
    //id("com.android.library")
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

    //androidTarget {
    //    publishLibraryVariants("release")
    //}

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

        //named("androidMain") {
        //    dependencies {
        //        compileOnly("org.jetbrains.skiko:skiko-android:$skikoVersion")
        //        compileOnly("org.jetbrains.lets-plot:commons:$letsPlotVersion")
        //        compileOnly("org.jetbrains.lets-plot:datamodel:$letsPlotVersion")
        //        compileOnly("org.jetbrains.lets-plot:plot-base:$letsPlotVersion")
        //        compileOnly("org.jetbrains.lets-plot:plot-builder:$letsPlotVersion")
        //        compileOnly("org.jetbrains.lets-plot:plot-stem:$letsPlotVersion")
        //    }
        //}
    }
}

//android {
//    namespace = "org.jetbrains.letsPlot.skia.android"
//
//    compileSdk = (findProperty("android.compileSdk") as String).toInt()
//
//    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
//
//    defaultConfig {
//        minSdk = (findProperty("android.minSdk") as String).toInt()
//    }
//
//    buildTypes {
//        getByName("release") {
//            isMinifyEnabled = false // true - error: when compiling demo cant resolve classes
////            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
//        }
//    }
//
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_11
//        targetCompatibility = JavaVersion.VERSION_11
//    }
//
//    kotlin {
//        jvmToolchain(11)
//    }
//}


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