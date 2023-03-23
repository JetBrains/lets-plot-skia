/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

val lets_plot_version: String by extra
val skiko_version: String by extra

kotlin {
    // Can't remove jvm target:
    // org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformProjectConfigurationException:
    // Please initialize at least one Kotlin target in 'svg-mapper-skia (:svg-mapper-skia)'.
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                compileOnly("org.jetbrains.skiko:skiko:$skiko_version")

                compileOnly("org.jetbrains.lets-plot:base-portable:$lets_plot_version") { isTransitive = false }
                compileOnly("org.jetbrains.lets-plot:base:$lets_plot_version") { isTransitive = false }
                compileOnly("org.jetbrains.lets-plot:mapper-core:$lets_plot_version") { isTransitive = false }
                compileOnly("org.jetbrains.lets-plot:vis-svg-portable:$lets_plot_version") { isTransitive = false }
                compileOnly("org.jetbrains.lets-plot:vis-svg-mapper:$lets_plot_version") { isTransitive = false }
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
