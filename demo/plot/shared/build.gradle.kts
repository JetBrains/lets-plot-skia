/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("jvm")
}

val letsPlotVersion = extra["letsPlot.version"] as String
val letsPlotKotlinVersion = extra["letsPlotKotlin.version"] as String

dependencies {
    compileOnly("org.jetbrains.lets-plot:lets-plot-kotlin-kernel:$letsPlotKotlinVersion")

    compileOnly("org.jetbrains.lets-plot:commons:$letsPlotVersion")
    compileOnly("org.jetbrains.lets-plot:datamodel:$letsPlotVersion")

    testImplementation(kotlin("test"))
}
