# Lets-Plot Skia Frontend Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html). All scales should have the 'format' parameter.


## [1.0.3] - 2024-03-21

### Added

- Support for round `clip-path` for `coordPolar()`.
- Support for `geomCurve()`.

### Changed

Dev settings were updated:
- Gradle: v 8.6
- Kotlin: v1.9.22
- Android Gradle Plugin (AGP): v8.2.2 (see notes below)
- Compose Multiplatform: v1.6.1
- Androidx activity-compose: v1.8.2
- Skiko: v0.7.92 (see notes below)

- Lets-Plot Multiplatform: v4.3.0
- Lets-Plot Kotlin API: v4.7.0

> Notes:
>  - Minimum required JDK: 17.
>  - KMP is not yet compatible with AGP 8.3 and up.
>  - Skiko found to have issues with Android devtools (build, emulator):
>    - Skiko v0.7.93 and higher crashes in emulator on ARM arch.
>    - Skiko v0.7.98.1 crashes in emulator on x86 and AMR arch.


## [1.0.2] - 2023-11-30

### Fixed

- Panel flickering when updating data [[#6](https://github.com/JetBrains/lets-plot-skia/issues/6)].


## [1.0.1] - 2023-11-09

### Fixed

- Crashes in Android when rebuild a PlotPanel ("keep aspect ratio" or plot spec change).
- Unexpected redraw [[#2](https://github.com/JetBrains/lets-plot-skia/issues/2)].
- DisposableEffect is not called.


## [1.0.0] - 2023-10-05

### Added

- Support for Android, Compose Desktop and Java Swing platforms.
- Examples in a separate GitHub repository: [lets-plot-compose-demos](https://github.com/JetBrains/lets-plot-compose-demos).
