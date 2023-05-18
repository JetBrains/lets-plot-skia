import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
//    kotlin("multiplatform") // kotlin("jvm") doesn't work well in IDEA/AndroidStudio (https://github.com/JetBrains/compose-jb/issues/22)
    kotlin("jvm")
    id("org.jetbrains.compose")
}

val lets_plot_version: String by extra

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(project(":svg-mapper-skia"))
    implementation(project(":monolithic-skia-desktop"))

    implementation("org.jetbrains.lets-plot:base:$lets_plot_version") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:base-portable:$lets_plot_version") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-config-portable:$lets_plot_version") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-base-portable:$lets_plot_version") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-builder-portable:$lets_plot_version") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-builder:$lets_plot_version") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-common-portable:$lets_plot_version") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-config:$lets_plot_version") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:mapper-core:$lets_plot_version") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:vis-svg-mapper:$lets_plot_version") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:vis-svg-portable:$lets_plot_version") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-demo-common:$lets_plot_version") { isTransitive = false }
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
