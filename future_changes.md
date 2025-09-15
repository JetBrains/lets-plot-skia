## [3.0.0] - 2025-mm-dd

### Compatibility
                                                 
All artifacts were built with the following versions of dependencies:
- Compose Multiplatform : [1.8.2](https://github.com/JetBrains/compose-multiplatform/releases/tag/v1.8.2)
- Lets-Plot Kotlin API : [4.11.2](https://github.com/JetBrains/lets-plot-kotlin/releases/tag/v4.11.2)
- Lets-Plot Multiplatform : [4.7.3](https://github.com/JetBrains/lets-plot/releases/tag/v4.7.3)


### Added

### Changed
                             
#### Android

- Removed dependency on Skiko.
- 

#### Desktop

- Pure compose implementation.
- The following artifacts are no longer published: 
  - `platf-skia-awt`
  - `lets-plot-swing-skia`

### Fixed
                                  
- When zooming the page with the mouse, a black layer appears when refreshing [[#12](https://github.com/JetBrains/lets-plot-skia/issues/12)]                                     
- When using a dark theme, white lines appear on the sides of the plot [[#37](https://github.com/JetBrains/lets-plot-skia/issues/37)]
- Plot rendering issues when switching between tabs in the tabbed pane [[#38](https://github.com/JetBrains/lets-plot-skia/issues/38)]
- Display problem of lets-plot-skia when switching pages [[#42](https://github.com/JetBrains/lets-plot-skia/issues/42)]
- Markdown: missing bold and italic text style support [[#44](https://github.com/JetBrains/lets-plot-skia/issues/44)]
