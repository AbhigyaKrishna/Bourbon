plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.compose.compiler)
}

composeCompiler {
    enableStrongSkippingMode = true
    includeSourceInformation = true
}

android {
    namespace = rootProject.group.toString()

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.bundles.android)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.square.workflow)
}