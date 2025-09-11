## [3.0.0] - 2025-mm-dd

### Compatibility

- [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform) 1.8.2
- [Lets-Plot Kotlin API](https://github.com/JetBrains/lets-plot-kotlin) 4.11.1
- [Lets-Plot Multiplatform](https://github.com/JetBrains/lets-plot) 4.7.2

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
- (???) Error when running ggsave [[#30](https://github.com/JetBrains/lets-plot-skia/issues/30)]
- When using a dark theme, white lines appear on the sides of the plot [[#37](https://github.com/JetBrains/lets-plot-skia/issues/37)]
- Plot rendering issues when switching between tabs in the tabbed pane [[#38](https://github.com/JetBrains/lets-plot-skia/issues/38)]
- Display problem of lets-plot-skia when switching pages [[#42](https://github.com/JetBrains/lets-plot-skia/issues/42)]
