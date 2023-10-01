[![Experimental](https://kotl.in/badges/experimental.svg)](https://kotlinlang.org/docs/components-stability.html)
[![JetBrains incubator project](https://jb.gg/badges/incubator.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![License MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://raw.githubusercontent.com/JetBrains/lets-plot-skia/master/LICENSE)


# Lets-Plot Skia Frontend

**Lets-Plot Skia Frontend** is a Kotlin Multiplatform library that allows you to embed \
[Lets-Plot](https://github.com/JetBrains/lets-plot) charts in a [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform) or Java Swing application.

### Supported Targets
- **Compose Desktop** (macOS, Windows, Linux)\
  For more information see [Compose multiplatform compatibility and versioning overview](https://github.com/JetBrains/compose-multiplatform/blob/master/VERSIONING.md). 
- **Android**
- **Java Swing**

![Splash](img-2.png)

## Dependencies

- [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform) (v. 1.5.1 and later)
- [Skiko](https://github.com/JetBrains/skiko) (v. 0.7.80)
- [Lets-Plot Kotlin API](https://github.com/JetBrains/lets-plot-kotlin) (v. 4.4.3+)
- [Lets-Plot Multiplatform](https://github.com/JetBrains/lets-plot) (v. 4.0.1+)

## Using as Dependency

### Compose Desktop

```kotlin
dependencies {
    ...

    // Lets-Plot Kotlin API 
    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-kernel:4.4.3")

    // Lets-Plot Backend 
    implementation("org.jetbrains.lets-plot:lets-plot-common:4.0.1")
    implementation("org.jetbrains.lets-plot:platf-awt:4.0.1")

    // Lets-Plot Skia Frontend
    implementation("org.jetbrains.lets-plot:lets-plot-compose:1.0.0")
}
```

### Android

```kotlin
dependencies {
    ...

    implementation("org.jetbrains.skiko:skiko-android:0.7.80")

    // Lets-Plot Kotlin API 
    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-kernel:4.4.3")

    // Lets-Plot Backend 
    implementation("org.jetbrains.lets-plot:lets-plot-common:4.0.1")

    // Lets-Plot Skia Frontend
    implementation("org.jetbrains.lets-plot:lets-plot-compose:1.0.0")
}
```

### Java Swing

```kotlin
dependencies {
    ...

    implementation("org.jetbrains.skiko:skiko:0.7.80")
    // The host OS and architecture should be specified explicitly.
    implementation("org.jetbrains.skiko:skiko-awt-runtime-macos-x64:0.7.80")

    // Lets-Plot Kotlin API 
    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-kernel:4.4.3")

    // Lets-Plot Backend 
    implementation("org.jetbrains.lets-plot:lets-plot-common:4.0.1")
    implementation("org.jetbrains.lets-plot:platf-awt:$letsPlotVersion")

    // Lets-Plot Skia Frontend
    implementation(project(":lets-plot-swing-skia"))
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
Copyright © 2019-2023, JetBrains s.r.o.
