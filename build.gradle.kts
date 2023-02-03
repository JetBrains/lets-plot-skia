import java.util.*

buildscript {

}

plugins {
    kotlin("jvm") apply false
    kotlin("android") apply false
    id("com.android.application") apply false
}

val localProps = Properties()
if (project.file("local.properties").exists()) {
    localProps.load(project.file("local.properties").inputStream())
    project.extra["local"] = localProps
}

allprojects {
    group = "me.ikupriyanov"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
        mavenLocal()

        localProps["maven.repo.local"]?.let {
            mavenLocal {
                url = uri(it)
            }
        }
    }
}

subprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
