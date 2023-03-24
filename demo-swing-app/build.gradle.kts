plugins {
    application
    kotlin("jvm")
}

val lets_plot_version: String by extra
val skiko_version: String by extra

val osName = System.getProperty("os.name")
val hostOs = when {
    osName == "Mac OS X" -> "macos"
    osName.startsWith("Win") -> "windows"
    osName.startsWith("Linux") -> "linux"
    else -> error("Unsupported OS: $osName")
}

val osArch = System.getProperty("os.arch")
var hostArch = when (osArch) {
    "x86_64", "amd64" -> "x64"
    "aarch64" -> "arm64"
    else -> error("Unsupported arch: $osArch")
}

val host = "${hostOs}-${hostArch}"


dependencies {
    implementation("org.jetbrains.skiko:skiko:$skiko_version")
    implementation(project(":svg-mapper-skia"))
    implementation(project(":monolithic-skia"))

    implementation("org.jetbrains.skiko:skiko-awt-runtime-$hostOs-$hostArch:$skiko_version")
    implementation("org.jetbrains.lets-plot:base:$lets_plot_version") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:base-portable:$lets_plot_version") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-base-portable:$lets_plot_version") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:vis-svg-portable:$lets_plot_version") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:vis-svg-mapper:$lets_plot_version") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-builder-portable:$lets_plot_version") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-builder:$lets_plot_version") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-config:$lets_plot_version") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-config-portable:$lets_plot_version") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-common-portable:$lets_plot_version") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:mapper-core:$lets_plot_version") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-demo-common:$lets_plot_version") { isTransitive = false }
}
