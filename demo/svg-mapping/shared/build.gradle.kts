/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("jvm")
}

val letsPlotVersion = extra["letsPlot.version"] as String

dependencies {
    // Property, SimpleComposite, FontFamily
    compileOnly("org.jetbrains.lets-plot:base-portable:$letsPlotVersion") { isTransitive = false }

    // SvgSvgElement, SvgNodeContainer, SvgGElement, SvgTextElement, etc
    compileOnly("org.jetbrains.lets-plot:vis-svg-portable:$letsPlotVersion") { isTransitive = false }

    implementation("io.github.microutils:kotlin-logging-jvm:2.0.5") // TODO remove with other { isTransitive = false }

    testImplementation(kotlin("test"))
}
