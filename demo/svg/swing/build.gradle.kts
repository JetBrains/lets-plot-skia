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

    implementation(project(":skia-svg-mapper"))
    implementation(project(":skia-awt"))

    implementation(project(":demo-svg-shared"))

    implementation("org.jetbrains.lets-plot:lets-plot-common:$letsPlotVersion")
}
