# lets-plot-skia-mapper

Rendering lets-plot with Skia. This project is a WIP.

![img.png](img.png)


### Local configuration for Lets-Plot project.
Skia mapper requires additional modules that have to be published. Checkout the `for-skiko-mapper` branch in `lets-plot` project directory and run the following command:
> ./gradlew publishAllPublicationsToMavenLocalRepository

### Local configuration for Skia mapper project.
Add the following property `maven.repo.local=<LETS-PLOT-PROJECT-ROOT>/.maven-publish-dev-repo` to the `local.properties` file.


### Android demo configuration.

With `SDK Manager` from menu "Tools -> Android -> SDK Manager" setup Android SDK. `local.properties` file should be automatically generated. Otherwise add it manually with proper path (like `sdk.dir=/Users/john/Library/Android/sdk`).  

With `Device Manager` from "Tools -> Android -> Device Manager" setup virtual device.   

Nexus 10 with Android 12 works well.

Select `demo-android-app` in `Run configurations` to run it.

![](android_demo.gif)


### See also

[Compose-multiplatform iOS/Android Template](https://github.com/JetBrains/compose-multiplatform-ios-android-template)  
[Compose-multiplatform Versioning](https://github.com/JetBrains/compose-multiplatform/blob/master/VERSIONING.md)

### Problems with updating tools and dependencies:  
#### Gradle:
Can't be updated to version `8.1.1` because of error:  
https://youtrack.jetbrains.com/issue/IDEA-319618/Unable-to-find-Gradle-tasks-to-build-xxx-Build-mode-COMPILEJAVA-when-running-a-Kotlin-file-in-a-project-using-android

#### Skiko:
Can't be updated to version `0.7.67+` because it crashes on Android:  
https://github.com/JetBrains/skiko/issues/761

#### AGP:  
Can't be updated to version `8.0.0` because IDEA doesn't support it:  
https://youtrack.jetbrains.com/issue/IDEA-317997

#### Compose Multiplatform:
Since version `1.4.1`, there is a problem when using `repaintDelay` with a value other than 0. For some reason, the plot does not display.