/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("jvm")
}

val skikoVersion = extra["skiko.version"] as String
val letsPlotVersion = extra["letsPlot.version"] as String

dependencies {
    compileOnly("org.jetbrains.skiko:skiko:$skikoVersion")

    implementation(project(":skia-svg-mapper"))

    compileOnly("org.jetbrains.lets-plot:lets-plot-common:$letsPlotVersion")
    compileOnly("org.jetbrains.lets-plot:platf-awt:$letsPlotVersion")

    testImplementation(kotlin("test"))
}
