import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kembeddable)
}

group = "cn.enaium"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    maven("https://repo.maven.rtast.cn/releases")
}

kembeddable {
    resourcePath.set(listOf(File("src/commonMain/resources")))
    compression = true
    packageName = "cn.enaium.waveformvisualizer.generated"
}

kotlin {
    macosArm64()
    linuxX64()
    linuxArm64()
    mingwX64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.filekit.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.webview.kmp)
        }
        macosArm64Main.dependencies {
            implementation(libs.filekit.dialogs)
        }
        mingwX64Main.dependencies {
            implementation(libs.filekit.dialogs)
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.executable {
            entryPoint = "cn.enaium.waveformvisualizer.main"
        }
    }
}

val buildReact = tasks.register<Exec>("buildReact") {
    description = ""
    workingDir = file("src/commonMain/react")
    commandLine(
        Runtime.getRuntime().exec(arrayOf("which", "bun")).inputStream.reader().readText().trim(),
        "run",
        "build"
    )
}

tasks.named("generateResources") {
    dependsOn(buildReact)
}

tasks.withType<KotlinNativeCompile>().configureEach {
    dependsOn("generateResources")
}
