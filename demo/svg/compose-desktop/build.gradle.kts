//import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
//    kotlin("multiplatform") // kotlin("jvm") doesn't work well in IDEA/AndroidStudio (https://github.com/JetBrains/compose-jb/issues/22)
    kotlin("jvm")
    id("org.jetbrains.compose")
}

val letsPlotVersion = extra["letsPlot.version"] as String

dependencies {
    implementation(compose.desktop.currentOs)

    implementation(project(":platf-skia"))
    implementation(project(":platf-skia-awt"))

    implementation(project(":demo-svg-shared"))

    implementation("org.jetbrains.lets-plot:lets-plot-common:$letsPlotVersion")
}

//compose.desktop.application {
//    //mainClass = "me.ikupriyanov.demo.plot.AreaViewerSkiaKt"
//    //mainClass = "me.ikupriyanov.demo.svg.InteractiveSvgDemoKt"
//
//    nativeDistributions {
//        targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
//        packageName = "lets-plot-compose-demo-app"
//        packageVersion = "1.0.0"
//    }
//}
