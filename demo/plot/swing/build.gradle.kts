plugins {
    kotlin("jvm")
}

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

val skikoVersion = extra["skiko.version"] as String
val letsPlotVersion = extra["letsPlot.version"] as String
val letsPlotKotlinVersion = extra["letsPlotKotlin.version"] as String

dependencies {
    implementation("org.jetbrains.skiko:skiko:$skikoVersion")
    implementation("org.jetbrains.skiko:skiko-awt-runtime-$hostOs-$hostArch:$skikoVersion")

    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-kernel:$letsPlotKotlinVersion")

    implementation(project(":platf-skia"))
    implementation(project(":platf-skia-awt"))
    implementation(project(":plot-swing"))

    implementation(project(":demo-plot-shared"))

    implementation("org.jetbrains.lets-plot:lets-plot-common:$letsPlotVersion")
    implementation("org.jetbrains.lets-plot:platf-awt:$letsPlotVersion")
}
