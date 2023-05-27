import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.1.3")
    }
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

plugins {
    id("com.android.application")
    kotlin("android")
}

val skikoNativeX64 by configurations.creating
val skikoNativeArm64 by configurations.creating

val jniDir = "${projectDir.absolutePath}/src/main/jniLibs"

// TODO: filter .so files only.
val unzipTaskX64 = tasks.register("unzipNativeX64", Copy::class) {
    destinationDir = file("$jniDir/x86_64")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE // TODO: fremove when filter by .so will start to work
    from(skikoNativeX64.map { zipTree(it) })
}

val unzipTaskArm64 = tasks.register("unzipNativeArm64", Copy::class) {
    destinationDir = file("$jniDir/arm64-v8a")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE // TODO: fremove when filter by .so will start to work
    from(skikoNativeArm64.map { zipTree(it) })
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 27
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        applicationId = "me.ikupriyanov.letsPlot.demo"

        ndk {
            abiFilters += listOf("x86_64", "arm64-v8a")
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

val skikoVersion = extra["skiko.version"] as String
val letsPlotVersion = extra["letsPlot.version"] as String


// ./gradlew -Pskiko.android.enabled=true \
//    publishSkikoJvmRuntimeAndroidX64PublicationToMavenLocal \
//    publishSkikoJvmRuntimeAndroidArm64PublicationToMavenLocal \
//    publishAndroidPublicationToMavenLocal
dependencies {
    implementation("org.jetbrains.skiko:skiko-android:$skikoVersion")
    skikoNativeX64("org.jetbrains.skiko:skiko-android-runtime-x64:$skikoVersion")
    skikoNativeArm64("org.jetbrains.skiko:skiko-android-runtime-arm64:$skikoVersion")

    implementation(project(":svg-mapper-skia"))
    implementation(project(":monolithic-skia-android"))

    implementation("org.jetbrains.lets-plot:base-portable:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:base:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:mapper-core:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:vis-svg-portable:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:vis-svg-mapper:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-base-portable:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-common-portable:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-config-portable:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-builder-portable:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-builder:$letsPlotVersion") { isTransitive = false }
    implementation("org.jetbrains.lets-plot:plot-demo-common:$letsPlotVersion") { isTransitive = false }

    implementation("io.github.microutils:kotlin-logging-jvm:2.0.5") // TODO remove with other { isTransitive = false }
}

tasks.withType<KotlinJvmCompile>().configureEach {
    dependsOn(unzipTaskX64)
    dependsOn(unzipTaskArm64)
}