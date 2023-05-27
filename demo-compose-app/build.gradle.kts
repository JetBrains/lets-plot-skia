import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
//    kotlin("multiplatform") // kotlin("jvm") doesn't work well in IDEA/AndroidStudio (https://github.com/JetBrains/compose-jb/issues/22)
    kotlin("jvm")
    id("org.jetbrains.compose")
}

val letsPlotVersion = extra["letsPlot.version"] as String

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(project(":svg-mapper-skia"))
    implementation(project(":monolithic-skia-desktop"))

    implementation("org.jetbrains.lets-plot:base:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:base-portable:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-config-portable:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-base-portable:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-builder-portable:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-builder:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-common-portable:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-config:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:mapper-core:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:vis-svg-mapper:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:vis-svg-portable:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-demo-common:$letsPlotVersion") { isTransitive = false }
}

compose.desktop.application {
    //mainClass = "me.ikupriyanov.demo.plot.AreaViewerSkiaKt"
    //mainClass = "me.ikupriyanov.demo.svg.InteractiveSvgDemoKt"

    nativeDistributions {
        targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
        packageName = "lets-plot-compose-demo-app"
        packageVersion = "1.0.0"
    }
}
