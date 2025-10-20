plugins {
//    kotlin("multiplatform") // kotlin("jvm") doesn't work well in IDEA/AndroidStudio (https://github.com/JetBrains/compose-jb/issues/22)
    kotlin("jvm")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
}

val letsPlotVersion = extra["letsPlot.version"] as String
val letsPlotKotlinVersion = extra["letsPlotKotlin.version"] as String

dependencies {
    implementation(compose.desktop.currentOs)

    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-kernel:$letsPlotKotlinVersion")
    implementation("org.jetbrains.lets-plot:lets-plot-common:$letsPlotVersion")

    implementation(project(":lets-plot-compose"))
    implementation("org.jetbrains.lets-plot:canvas:0.0.0-SNAPSHOT")
    implementation("org.jetbrains.lets-plot:plot-raster:0.0.0-SNAPSHOT")

    implementation(project(":demo-plot-shared"))

    implementation("org.slf4j:slf4j-simple:2.0.9")  // Enable logging to console
}
