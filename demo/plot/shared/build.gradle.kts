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
    // Property, SimpleComposite, FontFamily
    compileOnly("org.jetbrains.lets-plot:base-portable:$letsPlotVersion") { isTransitive = false }

    // SvgSvgElement, SvgNodeContainer, SvgGElement, SvgTextElement, etc
    compileOnly("org.jetbrains.lets-plot:vis-svg-portable:$letsPlotVersion") { isTransitive = false }

    compileOnly("org.jetbrains.lets-plot:lets-plot-kotlin-kernel:$letsPlotKotlinVersion") { isTransitive = false }

    testImplementation(kotlin("test"))
}
