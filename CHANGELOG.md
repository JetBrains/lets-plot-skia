# Lets-Plot Skia Frontend Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html). All scales should have the 'format' parameter.


## [1.0.0] - 2023-10-05

### Added

- Support for Android, Compose Desktop and Java Swing platforms.
- Examples in a separate GitHub repository: [lets-plot-compose-demos](https://github.com/JetBrains/lets-plot-compose-demos).


## [1.0.1] - 2023-11-09

### Fixed

- Crashes in Android when rebuild a PlotPanel ("keep aspect ratio" or plot spec change).
- Unexpected redraw [[#2](https://github.com/JetBrains/lets-plot-skia/issues/2)].
- DisposableEffect is not called.