# Lets-Plot Skia Frontend

[![Experimental](https://kotl.in/badges/experimental.svg)](https://kotlinlang.org/docs/components-stability.html)
[![JetBrains incubator project](https://jb.gg/badges/incubator.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![License MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://raw.githubusercontent.com/JetBrains/lets-plot-skia/master/LICENSE)
[![Latest Release](https://img.shields.io/github/v/release/JetBrains/lets-plot-skia)](https://github.com/JetBrains/lets-plot-skia/releases/latest)

**Lets-Plot Skia Frontend** is a Kotlin Multiplatform library that allows you to embed \
[Lets-Plot](https://github.com/JetBrains/lets-plot) charts in a [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform) or Java Swing application.

### Supported Targets
- **Compose Desktop** (macOS, Windows, Linux)\
  For more information see [Compose multiplatform compatibility and versioning overview](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-compatibility-and-versioning.html). 
- **Android** (`lets-plot-skia` <= v2.0.0. New versions temporarily don't support Android due to [SKIKO-761](https://youtrack.jetbrains.com/issue/SKIKO-761))  
- **Java Swing**

![Splash](img-2.png)

## Dependencies

See release notes for the latest version of the dependencies: [v2.1.0](https://github.com/JetBrains/lets-plot-skia/releases/tag/v2.1.0)

### Compose Desktop

```kotlin
dependencies {
    ...

    // Lets-Plot Kotlin API 
    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-kernel:4.9.2")

    // Lets-Plot Multiplatform 
    implementation("org.jetbrains.lets-plot:lets-plot-common:4.5.1")
    implementation("org.jetbrains.lets-plot:platf-awt:4.5.1")

    // Lets-Plot Skia Frontend
    implementation("org.jetbrains.lets-plot:lets-plot-compose:2.1.0")
}
```
See example: [Compose desktop](https://github.com/JetBrains/lets-plot-compose-demos/blob/main/compose-desktop/build.gradle.kts) demo.

### Compose Android

#### Note: the latest supported version is `lets-plot-skia` v2.0.0. The new versions temporarily don't support Android due to [SKIKO-761](https://youtrack.jetbrains.com/issue/SKIKO-761).

```kotlin
dependencies {
    ...

    implementation("org.jetbrains.skiko:skiko-android:0.8.4")

    // Lets-Plot Kotlin API 
    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-kernel:4.9.2")

    // Lets-Plot Multiplatform 
    implementation("org.jetbrains.lets-plot:lets-plot-common:4.5.1")

    // Lets-Plot Skia Frontend
    implementation("org.jetbrains.lets-plot:lets-plot-compose:2.0.0")
}
```

See example: [Android minimal](https://github.com/JetBrains/lets-plot-compose-demos/blob/main/compose-android-min/build.gradle.kts) demo.

### Java Swing

```kotlin
dependencies {
    ...

    implementation("org.jetbrains.skiko:skiko:0.8.4")
    // The host OS and architecture should be specified explicitly.
    implementation("org.jetbrains.skiko:skiko-awt-runtime-macos-x64:0.8.4")

    // Lets-Plot Kotlin API 
    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-kernel:4.9.2")

    // Lets-Plot Multiplatform 
    implementation("org.jetbrains.lets-plot:lets-plot-common:4.5.1")
    implementation("org.jetbrains.lets-plot:platf-awt:4.5.1")

    // Lets-Plot Skia Frontend
    implementation("org.jetbrains.lets-plot:lets-plot-swing-skia:2.1.0")
}
```

## Examples

You will find complete examples of using **Lets-Plot Kotlin API** with **Lets-Plot Skia Frontend** in the following\
GitHub repository: [JetBrains/lets-plot-compose-demos](https://github.com/JetBrains/lets-plot-compose-demos).

## Change Log

See [CHANGELOG.md](https://github.com/JetBrains/lets-plot-skia/blob/master/CHANGELOG.md).

## Code of Conduct

This project and the corresponding community are governed by the
[JetBrains Open Source and Community Code of Conduct](https://confluence.jetbrains.com/display/ALL/JetBrains+Open+Source+and+Community+Code+of+Conduct).
Please make sure you read it.

## License

Code and documentation released under
the [MIT license](https://github.com/JetBrains/lets-plot-skia/blob/master/LICENSE).
Copyright © 2023-2024, JetBrains s.r.o.
