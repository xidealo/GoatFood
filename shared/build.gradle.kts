import CommonApplication.deploymentTarget

plugins {
    alias(libs.plugins.client.compose.multiplatform.feature)
    alias(libs.plugins.cocoa)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.mokkery)
}

kotlin {
    android {
        namespace = Namespace.shared
    }

    cocoapods {
        summary = "Main shared module with presentation layer"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = deploymentTarget
        podfile = project.file("../iosApp/Podfile")

        pod("FirebaseMessaging")
        pod("FirebaseCrashlytics")
        framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":analytic"))
                implementation(project(":core"))
                implementation(project(":designsystem"))
                implementation(project(":feature:menu"))
                implementation(project(":feature:profile"))
                implementation(project(":feature:productdetails"))
                implementation(project(":feature:auth"))
                implementation(project(":feature:address"))
                implementation(project(":feature:splash"))
                implementation(project(":feature:cafe"))
                implementation(project(":feature:update"))
                implementation(project(":feature:order"))
                implementation(project(":feature:createorder"))
                implementation(project(":feature:consumercart"))

                implementation(libs.bundles.ktor)

                implementation(libs.kotlinx.coroutines.core)

                implementation(libs.kotlinx.serialization.json)

                implementation(libs.kotlinx.datetime)

                implementation(libs.sqlDelight.runtime)
                implementation(libs.sqlDelight.coroutines.extensions)
                implementation(libs.sqlDelight.primitive.adapters)
                implementation(libs.sqlDelight.async.extensions)

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
                implementation(libs.koin.test)

                implementation(kotlin("test"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.ktor.client.okhttp)
                implementation(libs.datastore.preferences)
                implementation(libs.play.review.ktx)
                implementation(libs.lifecycle.viewmodel.ktx)

                implementation(libs.activity.compose)
                implementation(compose.uiTooling)
                implementation(project.dependencies.platform(libs.firebase.bom))
                implementation(libs.firebase.messaging)
                implementation(libs.firebase.crashlytics)
                implementation(libs.sqlDelight.android)
            }
        }

        val iosMain by getting {
            dependencies {
                implementation(libs.sqlDelight.native)
                implementation(libs.ktor.client.darwin)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(libs.ktor.client.js)
                implementation(libs.sqlDelight.sqljs)
                implementation(npm("@cashapp/sqldelight-sqljs-worker", libs.versions.sqlDelight.get()))
                implementation(npm("sql.js", "1.8.0"))
                implementation(devNpm("copy-webpack-plugin", "9.1.0"))
            }
        }
    }
}

sqldelight {
    databases {
        create("FoodDeliveryDatabase") {
            packageName.set("com.bunbeauty.shared.db")
            generateAsync.set(true)
        }
    }
}
