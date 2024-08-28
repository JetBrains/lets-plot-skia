### Configuring IntelliJ IDEA for Android development

- #### Android Plugin

Make sure the [Android plugin](https://plugins.jetbrains.com/plugin/22989-android) is installed.

- #### Android SDK

From menu `Tools -> Android -> SDK Manager` setup an Android SDK.

The `local.properties` file will be automatically generated.
In case the `local.properties` file didn't appear in the project root:
- create it manually
- add property `sdk.dir` pointing to the location of the Android SDK on your system.

For example:
```
sdk.dir=/Users/john/Library/Android/sdk
```

- #### Android Device

From menu `Tools -> Android -> Device Manager` setup Android device.

For example, Nexus 10 with Android 12 works well.

- #### Java 11 Toolchain

Make sure the Java 11 toolchain is installed. Otherwise, the following error may occur:
```
> Cannot find a Java installation on your machine matching this tasks requirements: {languageVersion=11, vendor=any, implementation=vendor-specific} ...
  > No locally installed toolchains match and toolchain download repositories have not been configured.
```


### Running Android demos

In `Run configurations`
- Select `demo-plot-compose-android-min` or `demo-plot-compose-android-median` application
- Select the `Android` device
- Click `Run`

### See also

[Compose-multiplatform iOS/Android Template](https://github.com/JetBrains/compose-multiplatform-ios-android-template)  
[Compose-multiplatform Versioning](https://github.com/JetBrains/compose-multiplatform/blob/master/VERSIONING.md)

### Problems with updating tools and dependencies:

#### Android:
Can't upgrade Skiko version 0.7.80 --> 0.7.81 :
> java.lang.UnsatisfiedLinkError: dlopen failed: cannot locate symbol "_ZN4sksg4NodeD2Ev"

> org.jetbrains.skia.Paint.<clinit>(Paint.kt:10)

#### Compose Desktop:
Since JetBrains compose version `1.4.1`, `PlotComponentProvider` do not show plot if the `repaintDelay` value is grater than 0.
The reason is yet unknown.
                                      

