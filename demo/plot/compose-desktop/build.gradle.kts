plugins {
//    kotlin("multiplatform") // kotlin("jvm") doesn't work well in IDEA/AndroidStudio (https://github.com/JetBrains/compose-jb/issues/22)
    kotlin("jvm")
    id("org.jetbrains.compose")
}

val letsPlotVersion = extra["letsPlot.version"] as String
val letsPlotKotlinVersion = extra["letsPlotKotlin.version"] as String

dependencies {
    implementation(compose.desktop.currentOs)

    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-kernel:$letsPlotKotlinVersion")

    implementation(project(":skia-svg-mapper"))
    implementation(project(":plot-compose"))

    implementation(project(":demo-plot-shared"))

    implementation("org.jetbrains.lets-plot:lets-plot-common:$letsPlotVersion")
    implementation("org.jetbrains.lets-plot:platf-awt:$letsPlotVersion")
}
