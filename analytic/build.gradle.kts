import CommonApplication.deploymentTarget
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.cocoa)
    alias(libs.plugins.android.kmp.library)
}

val generateAppMetricaApiKey =
    tasks.register("generateAppMetricaApiKey") {
        val propertiesFile = rootProject.layout.projectDirectory.file("local.properties")
        val outputDirectory = layout.buildDirectory.dir("generated/appmetrica/kotlin")
        inputs.file(propertiesFile).optional()
        outputs.dir(outputDirectory)
        doLast {
            val propertiesSource = propertiesFile.asFile
            val apiKey =
                if (propertiesSource.isFile) {
                    val properties = Properties()
                    propertiesSource.inputStream().use { input -> properties.load(input) }
                    properties.getProperty("APP_METRICA_API_KEY").orEmpty().trim()
                } else {
                    ""
                }
            val escapedKey =
                buildString {
                    apiKey.forEach { character ->
                        when (character) {
                            '\\' -> append("\\\\")
                            '"' -> append("\\\"")
                            '$' -> append("\\$")
                            '\n' -> append("\\n")
                            '\r' -> append("\\r")
                            else -> append(character)
                        }
                    }
                }
            val packageDirectory = outputDirectory.get().asFile.resolve("com/bunbeauty/analytic")
            packageDirectory.mkdirs()
            packageDirectory.resolve("AppMetricaApiKeyValue.kt").writeText(
                """
                package com.bunbeauty.analytic

                internal const val APP_METRICA_API_KEY = "$escapedKey"

                """.trimIndent(),
            )
        }
    }

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    applyDefaultHierarchyTemplate()
    android {
        namespace = "com.bunbeauty.analytic"
        compileSdk {
            version =
                release(AndroidSdk.compile) {
                    minorApiLevel = AndroidSdk.compileMinor
                }
        }
        minSdk = AndroidSdk.min
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
        androidResources {
            enable = true
        }
        withHostTest {}
    }

    iosArm64()
    iosSimulatorArm64()

    js(IR) {
        browser()
    }

    cocoapods {
        summary = "Analytic module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = deploymentTarget

        podfile = project.file("../iosApp/Podfile")

        pod("FirebaseAnalytics")
        pod("AppMetricaCore") {
            version = "~> 7.0.0"
        }
        framework {
            baseName = "analytic"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            kotlin.srcDir(generateAppMetricaApiKey)
            dependencies {
                implementation(project(":core"))
                implementation(libs.koin.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(project.dependencies.platform(libs.firebase.bom))
                implementation(libs.firebase.analytics)
                implementation(libs.appmetrica.analytics)
            }
        }
    }
}
