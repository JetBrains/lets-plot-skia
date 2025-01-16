plugins {
    kotlin("jvm")
}

val letsPlotVersion = extra["letsPlot.version"] as String
val letsPlotKotlinVersion = extra["letsPlotKotlin.version"] as String
val kotlinLoggingVersion = extra["kotlinLogging.version"] as String

dependencies {
    implementation(project(":demo-plot-shared"))
    implementation(project(":lets-plot-raster"))

    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-kernel:$letsPlotKotlinVersion")
    implementation("org.jetbrains.lets-plot:lets-plot-common:$letsPlotVersion")
    implementation("org.jetbrains.lets-plot:platf-awt-jvm:$letsPlotVersion")
    implementation("org.jetbrains.lets-plot:canvas:$letsPlotVersion")
    implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")

}
