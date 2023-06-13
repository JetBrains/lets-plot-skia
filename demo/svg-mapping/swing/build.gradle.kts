plugins {
    kotlin("jvm")
}

val skikoVersion = extra["skiko.version"] as String
val letsPlotVersion = extra["letsPlot.version"] as String

val osName = System.getProperty("os.name")!!
val hostOs = when {
    osName == "Mac OS X" -> "macos"
    osName.startsWith("Win") -> "windows"
    osName.startsWith("Linux") -> "linux"
    else -> error("Unsupported OS: $osName")
}

var hostArch = when (val osArch = System.getProperty("os.arch")) {
    "x86_64", "amd64" -> "x64"
    "aarch64" -> "arm64"
    else -> error("Unsupported arch: $osArch")
}

val host = "${hostOs}-${hostArch}"


dependencies {
    implementation("org.jetbrains.skiko:skiko:$skikoVersion")
    implementation("org.jetbrains.skiko:skiko-awt-runtime-$hostOs-$hostArch:$skikoVersion")

    implementation(project(":svg-mapper-skia"))
    implementation(project(":skia-awt"))

    implementation(project(":demo-svg-mapping-shared"))

    implementation("org.jetbrains.lets-plot:base:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:base-portable:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:vis-svg-portable:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:vis-svg-mapper:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:mapper-core:$letsPlotVersion") { isTransitive = false }

    // For components in demo: GroupComponent, Text, TextLabel
    implementation("org.jetbrains.lets-plot:plot-base-portable:$letsPlotVersion") { isTransitive = false }
    // plot.builder.presentation.Style
    implementation("org.jetbrains.lets-plot:plot-builder-portable:$letsPlotVersion") { isTransitive = false }

    // plot.builder.tooltip.TooltipBox
    implementation("org.jetbrains.lets-plot:plot-builder:$letsPlotVersion") { isTransitive = false }
}
