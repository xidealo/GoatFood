plugins {
    alias(libs.plugins.application) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.triplet.play) apply false
    alias(libs.plugins.google.service) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.crashlytics) apply false
    alias(libs.plugins.ktLint) apply false
    alias(libs.plugins.multiplatform) apply false
    alias(libs.plugins.cocoa) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.mokkery) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.kmp.library) apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// Xcode 27+ rejects IPHONEOS_DEPLOYMENT_TARGET below 15.0. The Kotlin CocoaPods plugin
// raises pod targets in the generated synthetic projects only to 12.0, and Firebase
// transitive podspecs (GoogleUtilities, nanopb, Promises) still declare 9.0-12.0, so
// podBuild* fails on modern Xcode. The synthetic Podfile is plugin-generated and cannot
// be edited, therefore raise the deployment target in the generated Pods project after
// `pod install` and before any podBuild* task runs.
subprojects {
    pluginManager.withPlugin("org.jetbrains.kotlin.native.cocoapods") {
        val minDeploymentTarget = CommonApplication.deploymentTarget
        val patchSyntheticPodsDeploymentTarget =
            tasks.register("patchSyntheticPodsDeploymentTarget") {
                description =
                    "Raises IPHONEOS_DEPLOYMENT_TARGET in the generated synthetic Pods " +
                        "project to $minDeploymentTarget (Xcode 27+ minimum)"
                val podsProjectFile =
                    layout.buildDirectory
                        .file("cocoapods/synthetic/ios/Pods/Pods.xcodeproj/project.pbxproj")
                inputs.file(podsProjectFile).withPropertyName("podsProjectFile")
                outputs.upToDateWhen { false }
                mustRunAfter("podInstallSyntheticIos")
                doLast {
                    val podsProject = podsProjectFile.get().asFile
                    if (podsProject.exists()) {
                        val minParts = minDeploymentTarget.split(".").map { it.toInt() }
                        val deploymentTargetSetting =
                            Regex("""IPHONEOS_DEPLOYMENT_TARGET = ([0-9]+(?:\.[0-9]+)*);""")
                        val patched =
                            deploymentTargetSetting.replace(podsProject.readText()) { match ->
                                val parts = match.groupValues[1].split(".").map { it.toInt() }
                                val major = parts.getOrElse(0) { 0 }
                                val minor = parts.getOrElse(1) { 0 }
                                val minMajor = minParts.getOrElse(0) { 0 }
                                val minMinor = minParts.getOrElse(1) { 0 }
                                val isBelowMinimum =
                                    major < minMajor || (major == minMajor && minor < minMinor)
                                if (isBelowMinimum) {
                                    "IPHONEOS_DEPLOYMENT_TARGET = $minDeploymentTarget;"
                                } else {
                                    match.value
                                }
                            }
                        podsProject.writeText(patched)
                    }
                }
            }
        tasks.matching { it.name.startsWith("podBuild") }.configureEach {
            dependsOn(patchSyntheticPodsDeploymentTarget)
        }
    }
}
