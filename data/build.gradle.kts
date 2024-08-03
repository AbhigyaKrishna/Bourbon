plugins {
    bourbon.`android-library-conventions`
    alias(libs.plugins.jetbrains.kotlinx.serialization)
}

android {
    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                arguments.putAll(
                    setOf(
                        "room.schemaLocation" to "$projectDir/schemas",
                        "room.incremental" to "true"
                    )
                )
            }
        }
    }
}

dependencies {
    api(project(":domain"))
    api(platform(libs.firebase.bom))
    api(libs.bundles.firebase)
    implementation(libs.google.play.services.auth)
    implementation(libs.bundles.credentials)
    implementation(platform(libs.kotlinx.serialization.bom))
    implementation(libs.kotlinx.serialization.core)
}