### Skiko and Compose Multiplatform Versions  

| Compose Multiplatform                                                                                  | Skiko  |
|--------------------------------------------------------------------------------------------------------|--------|
| [1.7.0](https://repo1.maven.org/maven2/org/jetbrains/compose/ui/ui-desktop/1.7.0/ui-desktop-1.7.0.pom) | 0.8.15 |
| [1.7.1](https://repo1.maven.org/maven2/org/jetbrains/compose/ui/ui-desktop/1.7.1/ui-desktop-1.7.1.pom) | 0.8.18 |
| [1.7.3](https://repo1.maven.org/maven2/org/jetbrains/compose/ui/ui-desktop/1.7.3/ui-desktop-1.7.3.pom) | 0.8.18 |
| [1.8.0](https://repo1.maven.org/maven2/org/jetbrains/compose/ui/ui-desktop/1.8.0/ui-desktop-1.8.0.pom) | 0.9.4  |
| [1.8.1](https://repo1.maven.org/maven2/org/jetbrains/compose/ui/ui-desktop/1.8.1/ui-desktop-1.8.1.pom) | 0.9.4.2 |
| [1.8.2](https://repo1.maven.org/maven2/org/jetbrains/compose/ui/ui-desktop/1.8.2/ui-desktop-1.8.2.pom) | 0.9.4.2 |


Changing the version of `Skiko` or `Compose Multiplatform` may lead to compatibility issues and runtime errors like `MethodNotFound` or `ClassNotFound`. 

The safest way to avoid these issues is to use the version of `Skiko` that was used to build your version of `Compose Multiplatform`. 

To find the correct `Skiko` version:

1. Open the `.pom` file corresponding to your `Compose Multiplatform` version.
2. Search within the `.pom` file for the `Skiko` version.

For example, if you're using `ui-desktop-1.6.10.pom`, the link will be: 
>https://repo1.maven.org/maven2/org/jetbrains/compose/ui/ui-desktop/1.6.10/ui-desktop-1.6.10.pom  

At the bottom of the file, you will find the `skiko` version used to build the library.  
```xml
<dependency>
    <groupId>org.jetbrains.skiko</groupId>
    <artifactId>skiko-awt</artifactId>
    <version>0.8.4</version>
    <scope>compile</scope>
</dependency>
```


### Configuring IntelliJ IDEA for Android Development

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
>Cannot find a Java installation on your machine matching this tasks requirements: {languageVersion=11, vendor=any, implementation=vendor-specific}
> 
>No locally installed toolchains match and toolchain download repositories have not been configured.

<a id="building-skiko-for-android"></a>

### Running Android demos

In `Run configurations`
- Select `demo-plot-compose-android-min` or `demo-plot-compose-android-median` application
- Select the `Android` device
- Click `Run`

### See also

[Compose-multiplatform iOS/Android Template](https://github.com/JetBrains/compose-multiplatform-ios-android-template)  
[Compose-multiplatform Versioning](https://github.com/JetBrains/compose-multiplatform/blob/master/VERSIONING.md)

### Problems with updating tools and dependencies:


#### Compose Desktop:
Since JetBrains compose version `1.4.1`, `PlotComponentProvider` do not show plot if the `repaintDelay` value is grater than 0.
The reason is yet unknown.
                                      

