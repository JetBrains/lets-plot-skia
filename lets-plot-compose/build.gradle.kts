/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("com.android.library")
    id("org.jetbrains.compose")
    `maven-publish`
    signing
}

val skikoVersion = extra["skiko.version"] as String
val composeVersion = extra["compose.version"] as String
val letsPlotVersion = extra["letsPlot.version"] as String
val letsPlotKotlinVersion = extra["letsPlotKotlin.version"] as String
val kotlinLoggingVersion = extra["kotlinLogging.version"] as String

kotlin {
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }

    androidTarget {
        publishLibraryVariants("release")
    }

    sourceSets {
        named("commonMain") {
            dependencies {
                compileOnly(compose.runtime)
                compileOnly(compose.ui)

                compileOnly("org.jetbrains.lets-plot:lets-plot-kotlin-kernel:$letsPlotKotlinVersion")

                compileOnly("org.jetbrains.lets-plot:lets-plot-common:$letsPlotVersion")
            }
        }

        named("desktopMain") {
            dependencies {
                compileOnly(compose.desktop.currentOs)
                compileOnly("org.jetbrains.skiko:skiko:${skikoVersion}")

                api(project(":platf-skia"))
                api(project(":platf-skia-awt"))

                compileOnly("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
            }
        }

        named("androidMain") {
            dependencies {
                api(project(":platf-skia"))
                compileOnly("org.jetbrains.lets-plot:plot-raster:${letsPlotVersion}")
                compileOnly("org.jetbrains.lets-plot:canvas:${letsPlotVersion}")
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
            maven {
                // For SNAPSHOT publication use separate URL and credentials:
                if (version.toString().endsWith("-SNAPSHOT")) {
                    url = uri(rootProject.project.extra["mavenSnapshotPublishUrl"].toString())

                    credentials {
                        username = rootProject.project.extra["sonatypeUsername"].toString()
                        password = rootProject.project.extra["sonatypePassword"].toString()
                    }
                } else {
                    url = uri(rootProject.project.extra["mavenReleasePublishUrl"].toString())
                }
            }
        }
    }
}

signing {
    if (!(project.version as String).contains("SNAPSHOT")) {
        sign(publishing.publications)
    }
}