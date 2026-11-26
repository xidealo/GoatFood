package com.bunbeauty

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun KotlinMultiplatformAndroidLibraryExtension.configureKotlinAndroidLibrary() {
    compileSdk {
        setFoodDeliveryCompileSdk()
    }
    minSdk = AndroidSdk.MIN
    androidResources {
        enable = true
    }
    withHostTest {
        isReturnDefaultValues = true
    }
}

internal fun Project.configureKotlinCompiler() {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            freeCompilerArgs.add("-Xstring-concat=inline")
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
