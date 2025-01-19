/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import java.util.*

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    kotlin("multiplatform").apply(false)
    kotlin("plugin.compose").apply(false)
    kotlin("jvm").apply(false)
    id("org.jetbrains.compose").apply(false)

    kotlin("android").apply(false)
    id("com.android.application").apply(false)
    id("com.android.library").apply(false)

    id("io.codearte.nexus-staging").apply(false)
    id("io.github.gradle-nexus.publish-plugin")
}

val localProps = Properties()
if (project.file("local.properties").exists()) {
    localProps.load(project.file("local.properties").inputStream())
} else {
    error("Couldn't read local.properties")
}

allprojects {
    group = "org.jetbrains.lets-plot"
    version = "2.1.2-SNAPSHOT"
//    version = "0.0.0-SNAPSHOT" // for local publishing only

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    tasks.withType<JavaCompile>().all {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
}

tasks.register<Zip>("packageSkikoJniLibs") {
    archiveFileName = "skiko-jni-libs.zip"
    from(layout.projectDirectory.dir("skiko-jni-libs"))
    destinationDirectory = layout.projectDirectory
}

subprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")

        // Repositories where other projects publish their artifacts locally to.
        localProps["maven.repo.local"]?.let {
            (it as String).split(",").forEach { repo ->
                mavenLocal {
                    url = uri(repo)
                }
            }
        }

        // SNAPSHOTS
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")

        mavenLocal()
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
        // Disabled due to the error:
        // > Cannot change hierarchy of dependency configuration ':lets-plot-compose:desktopCompilationApi' after it has been included in dependency resolution.

        // Add LICENSE file to the META-INF folder inside published JAR files
        //tasks.filterIsInstance(org.gradle.jvm.tasks.Jar::class.java)
        //    .forEach {
        //        it.metaInf {
        //            from("$rootDir") {
        //                include("LICENSE")
        //            }
        //        }
        //    }

        // Configure publications
        if (name in listOf(
                "platf-skia-awt",
                "lets-plot-swing-skia",
            )
        ) {
            apply(plugin = "maven-publish")

            configure<PublishingExtension> {
                publications.forEach { pub ->
                    with(pub as MavenPublication) {
                        artifact(jarJavaDocs)

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
