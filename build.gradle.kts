/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import java.util.*

//buildscript {
//
//}

plugins {
    kotlin("jvm") apply false
//    kotlin("android") apply false
    id("com.android.application") apply false
}

val localProps = Properties()
if (project.file("local.properties").exists()) {
    localProps.load(project.file("local.properties").inputStream())
    project.extra["local"] = localProps
}

allprojects {
    group = "org.jetbrains"
    version = "1.0-SNAPSHOT"

//    repositories {
//        mavenCentral()
//        mavenLocal()
//
//        localProps["maven.repo.local"]?.let {
//            mavenLocal {
//                url = uri(it)
//            }
//        }
//    }

//    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
//        kotlinOptions {
//            jvmTarget = "11"
//        }
//    }
//
//    tasks.withType<JavaCompile>().all {
//        sourceCompatibility = "11"
//        targetCompatibility = "11"
//    }
}

subprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")

//        mavenLocal()
        localProps["maven.repo.local"]?.let {
            mavenLocal {
                url = uri(it)
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
}
