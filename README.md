# lets-plot-skia-mapper

Rendering lets-plot with Skia. This project is a WIP.

![img.png](img.png)


### Android demo configuration

- #### With `SDK Manager` 

From menu `Tools -> Android -> SDK Manager` setup an Android SDK.

The `local.properties` file will be automatically generated. 
In case the `local.properties` file didn't appear in the project root:
 - create it manually
 - add property `sdk.dir` pointing to the location of the Android SDK on your system. 

For example:
```
sdk.dir=/Users/john/Library/Android/sdk
```

- #### With `Device Manager`   
                          
From menu `Tools -> Android -> Device Manager` setup Android device.

For example, Nexus 10 with Android 12 works well.

### Running Android demos

In `Run configurations`
- Select `demo-plot-compose-android-min` or `demo-plot-compose-android-median` application
- Select the `Android` device
- Click `Run`

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

#### Compose Desktop:
Since version `1.4.1`, `PlotComponentProvider` do not show plot if the `repaintDelay` value is grater than 0. 
The reason is yet unknown.