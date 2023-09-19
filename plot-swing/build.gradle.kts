/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("jvm")
}

val skikoVersion = extra["skiko.version"] as String
val letsPlotVersion = extra["letsPlot.version"] as String
val letsPlotKotlinVersion = extra["letsPlotKotlin.version"] as String

dependencies {
    compileOnly("org.jetbrains.skiko:skiko:$skikoVersion")

    compileOnly("org.jetbrains.lets-plot:lets-plot-kotlin-kernel:$letsPlotKotlinVersion")

    implementation(project(":platf-skia"))
    implementation(project(":platf-skia-awt"))

    compileOnly("org.jetbrains.lets-plot:lets-plot-common:$letsPlotVersion")
    compileOnly("org.jetbrains.lets-plot:platf-awt:$letsPlotVersion")

    testImplementation(kotlin("test"))
}
