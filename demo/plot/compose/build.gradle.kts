plugins {
//    kotlin("multiplatform") // kotlin("jvm") doesn't work well in IDEA/AndroidStudio (https://github.com/JetBrains/compose-jb/issues/22)
    kotlin("jvm")
    id("org.jetbrains.compose")
}

val letsPlotVersion = extra["letsPlot.version"] as String
val letsPlotKotlinVersion = extra["letsPlotKotlin.version"] as String

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(project(":svg-mapper-skia")) // { isTransitive = false }
    implementation(project(":monolithic-skia-desktop")) // { isTransitive = false }

    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-kernel:$letsPlotKotlinVersion") { isTransitive = false }

    implementation(project(":demo-plot-shared"))

    implementation("org.jetbrains.lets-plot:base-portable:$letsPlotVersion") { isTransitive = false }

    implementation("org.jetbrains.lets-plot:base:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:mapper-core:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:vis-svg-portable:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:vis-svg-mapper:$letsPlotVersion") { isTransitive = false }

    implementation("org.jetbrains.lets-plot:plot-base-portable:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-common-portable:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-builder:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-builder-portable:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-config-portable:$letsPlotVersion") { isTransitive = false }
}