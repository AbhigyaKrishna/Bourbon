plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.kapt)
}

kapt {
    correctErrorTypes = true
    useBuildCache = true
    showProcessorStats = true
}

android {
    namespace = rootProject.group.toString()

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

}