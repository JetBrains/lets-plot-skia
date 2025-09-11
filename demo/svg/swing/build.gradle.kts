plugins {
    kotlin("jvm")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
}

val letsPlotVersion = extra["letsPlot.version"] as String

dependencies {
    implementation(compose.desktop.currentOs)
    compileOnly(compose.ui)

    implementation("org.jetbrains.lets-plot:lets-plot-common:$letsPlotVersion")

    implementation(project(":lets-plot-compose"))
    implementation(project(":demo-svg-shared"))
}
