plugins {
    bourbon.`android-library-conventions`
    alias(libs.plugins.jetbrains.kotlinx.serialization)
    alias(libs.plugins.google.secrets.gradle.plugin)
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
    implementation(libs.bundles.google.play.services)
    implementation(libs.bundles.credentials)
    implementation(libs.generative.ai)
    implementation(platform(libs.kotlinx.serialization.bom))
    implementation(libs.bundles.kotlinx.serialization)
}