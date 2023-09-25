/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import java.util.*

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    kotlin("multiplatform").apply(false)
    kotlin("jvm").apply(false)
    kotlin("android").apply(false)
    id("com.android.application").apply(false)
    id("com.android.library").apply(false)
    id("org.jetbrains.compose").apply(false)

    id("io.codearte.nexus-staging").apply(false)

    id("io.github.gradle-nexus.publish-plugin")
}

val localProps = Properties()
if (project.file("local.properties").exists()) {
    localProps.load(project.file("local.properties").inputStream())
}

allprojects {
    group = "org.jetbrains.lets-plot"
    version = "1.0.0-SNAPSHOT"

//    val version = version as String

//     Maven publication settings
//    val sonatypeSnapshotUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
//    val sonatypeReleaseUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
//    project.extra["sonatypeUrl"] = if (version.contains("SNAPSHOT")) sonatypeSnapshotUrl else sonatypeReleaseUrl
}

subprojects {
    repositories {
        // Register "local repositories" to create dependencies on dev lets-plot, lets-plot-kotlin
        localProps["maven.repo.local"]?.let {
            (it as String).split(",").forEach { repo ->
                mavenLocal {
                    url = uri(repo)
                }
            }
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    tasks.withType<JavaCompile>().all {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    val jarJavaDocs by tasks.creating(Jar::class) {
        archiveClassifier.set("javadoc")
        group = "lets plot"
        from("$rootDir/README.md")
    }

    // ------------------------------------------
    // Workaround for the error when signing published artifacts.
    // It seems to appear after switching to Gradle 8.3
    // For details see: https://github.com/gradle/gradle/issues/26091 :
    // Publishing a KMP project with signing fails with "Task ... uses this output of task ... without declaring an explicit or implicit dependency"
    // https://github.com/gradle/gradle/issues/26091
    tasks.withType<AbstractPublishToMaven>().configureEach {
        val signingTasks = tasks.withType<Sign>()
        mustRunAfter(signingTasks)
    }

    afterEvaluate {
        // Add LICENSE file to the META-INF folder inside published JAR files
        tasks.filterIsInstance(org.gradle.jvm.tasks.Jar::class.java)
            .forEach {
                it.metaInf {
                    from("$rootDir") {
                        include("LICENSE")
                    }
                }
            }

        // Configure publications
        if (name in listOf(
                "platf-skia",
                "platf-skia-awt",
                "platf-skia-android",
                "plot-compose",
                "plot-swing",
            )
        ) {
            apply(plugin = "maven-publish")
            apply(plugin = "signing")

            val artifactBaseName = when (project.name) {
                "plot-compose" -> "lets-plot-compose"
                "plot-swing" -> "lets-plot-skia-swing"
                else -> project.name
            }
            val artifactGroupId = project.group as String
            val artifactVersion = project.version as String

            val artifactPOMName = when (project.name) {
                "plot-compose" -> "Lets-Plot for Compose Multiplatform"
                "plot-swing" -> "Lets-Plot for Swing/Skia"
                else -> "Part of Lets-Plot/Skia"
            }
            val artifactPOMDescr = when (project.name) {
                "plot-compose" -> "Lets-Plot for Compose Multiplatform"
                "plot-swing" -> "Lets-Plot JVM package with Swing/Skia rendering"
                else -> "Part of Lets-Plot with Skia rendering package"
            }

            configure<PublishingExtension> {
                publications.forEach { pub ->
                    with(pub as MavenPublication) {
                        groupId = artifactGroupId
                        artifactId = if (artifactId.startsWith("plot-compose")) {
                            artifactId.replace(
                                "plot-compose",
                                "lets-plot-compose"
                            )
                        } else if (artifactId.startsWith("plot-swing")) {
                            artifactId.replace(
                                "plot-swing",
                                "lets-plot-swing"
                            )
                        } else {
                            artifactId
                        }

                        version = artifactVersion

                        // Add "javadocs" to each publication or Maven won't publish it.
                        artifact(jarJavaDocs)

                        pom {
                            name.set(artifactPOMName)
                            description.set(artifactPOMDescr)

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

                        // Sign all publications.
                        // signing.sign(it)
                        configure<SigningExtension> {
                            sign(pub)
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
    }
}


// Nexus publish plugin settings:
val sonatypeUsername = localProps["sonatype.username"] as String?
val sonatypePassword = localProps["sonatype.password"] as String?
if (!(sonatypeUsername.isNullOrBlank() || sonatypePassword.isNullOrBlank())) {
    nexusPublishing.repositories {
        sonatype {
            stagingProfileId.set("11c25ff9a87b89")
            username.set(sonatypeUsername)
            password.set(sonatypePassword)

            nexusUrl.set(uri("https://oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}
