### Skiko and Compose Multiplatform Versions

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

> [!IMPORTANT]
> 
> When updating the `Skiko` version, make sure to build and commit corresponding Skiko binaries.
> See the [Building Skiko for Android](#building-skiko-for-android) section for details.


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
### Building Skiko for Android 

> [!TIP]
> 
> Rebuild `Skiko` whenever you update the `Skiko` version.


To workaround for the [#761](https://github.com/JetBrains/skiko/issues/761) and use latest version of skiko library, we need to build the library manually. The process may be a bit tricky, but in general it works as follows:
- Install `NDK` and `CMake` using `Tools -> Android -> Android SDK Manager`, `SDK Tools` tab.
- Clone the [Skiko](git@github.com:JetBrains/skiko.git) repository
- Checkout compatible version (read the **Skiko and Compose Multiplatform versions** section above). 
- In the Skiko repository, run the following command: ```./gradlew publishToBuildRepo -Pskiko.android.enabled=true```
- Tricky part: build may fail due to the missing headers and libraries. In this case, you need to install the missing dependencies manually. Google may help you with that. After that, try to build the library again.
- After the build is successful, find the JARs. The path is something like this:    
 `/home/user/Projects/skiko/skiko/build/repo/org/jetbrains/skiko/skiko-android-runtime-arm64/0.0.0-SNAPSHOT/skiko-android-runtime-arm64-0.0.0-20240827.190357-1.jar`  
 `/home/user/Projects/skiko/skiko/build/repo/org/jetbrains/skiko/skiko-android-runtime-x64/0.0.0-SNAPSHOT/skiko-android-runtime-x64-0.0.0-20240827.190357-1.jar`
- Unzip `skiko-android-runtime-arm64-*.jar` and copy the `libskiko-android-arm64.so` to the `<PROJECT_ROOT>/skiko-jni-libs/arm64-v8a` directory ([link](https://github.com/JetBrains/lets-plot-skia/tree/main/jniLibs/arm64-v8a)).
- Unzip `skiko-android-runtime-x64-*.jar` and copy the `libskiko-android-x64.so` to the `<PROJECT_ROOT>/skiko-jni-libs/x86_64` directory ([link](https://github.com/JetBrains/lets-plot-skia/tree/main/skiko-jni-libs/x86_64)).

Build scripts of demos will automatically copy the native libraries to the correct location.


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
>
> org.jetbrains.skia.Paint.<clinit>(Paint.kt:10)

#### Compose Desktop:
Since JetBrains compose version `1.4.1`, `PlotComponentProvider` do not show plot if the `repaintDelay` value is grater than 0.
The reason is yet unknown.
                                      

