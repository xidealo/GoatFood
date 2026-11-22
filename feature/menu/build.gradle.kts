plugins {
    alias(libs.plugins.client.compose.multiplatform.feature)
    alias(libs.plugins.mokkery)
}

kotlin {
    android {
        namespace = "com.bunbeauty.feature.menu"
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":designsystem"))
                implementation(project(":analytic"))
                implementation(project(":core"))

                implementation(libs.kotlinx.collections.immutable)

                implementation(compose.components.resources)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.animation)
                implementation(compose.animationGraphics)
                implementation(compose.components.uiToolingPreview)

                implementation(libs.bundles.navigation)
                implementation(libs.bundles.di)
                implementation(libs.bundles.coil)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.test)
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.activity.compose)
                implementation(compose.uiTooling)
            }
        }
    }
}
