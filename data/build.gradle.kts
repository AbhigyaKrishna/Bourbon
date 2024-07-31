plugins {
    bourbon.`android-library-conventions`
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
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
}