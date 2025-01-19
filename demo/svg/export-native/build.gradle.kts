/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

val skikoVersion = extra["skiko.version"] as String
val letsPlotVersion = extra["letsPlot.version"] as String

kotlin {
    //linuxX64 {
    //    binaries {
    //        executable {
    //            // TODO: libskia.o is required on Linux, but not on MacOS.
    //            //linkerOpts += listOf("-L/usr/lib/x86_64-linux-gnu/", "-lfontconfig", "-L"+"/home/ikupriyanov/Downloads/default/targets/linux_x64/included", "-lskia", "--allow-shlib-undefined")
    //        }
    //    }
    //}

    macosArm64 {
        binaries {
            executable()
        }
    }

    macosX64() {
        binaries {
            executable()
        }
    }

    sourceSets {

        all {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
            dependencies {
                implementation("com.squareup.okio:okio:3.9.0")
                implementation("org.jetbrains.skiko:skiko:$skikoVersion")
                //implementation("org.jetbrains.lets-plot:commons:$letsPlotVersion")
                //implementation("org.jetbrains.lets-plot:datamodel:$letsPlotVersion")
                //implementation(project(":platf-skia"))
            }
        }
    }
}
