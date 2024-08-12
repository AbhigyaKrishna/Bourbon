plugins {
    bourbon.`android-library-conventions`
    alias(libs.plugins.jetbrains.kotlinx.serialization)
}

dependencies {
    implementation(platform(libs.kotlinx.serialization.bom))
    implementation(libs.bundles.kotlinx.serialization)
    implementation(libs.arsceneview)
}