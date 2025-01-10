plugins {
    kotlin("jvm")
}

val letsPlotVersion = extra["letsPlot.version"] as String


dependencies {
    implementation(project(":demo-svg-shared"))
    implementation(project(":lets-plot-raster"))

    implementation("org.jetbrains.lets-plot:lets-plot-common:$letsPlotVersion")
    implementation("org.jetbrains.lets-plot:platf-awt-jvm:$letsPlotVersion")
    implementation("org.jetbrains.lets-plot:canvas:$letsPlotVersion")
}
