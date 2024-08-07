plugins {
    bourbon.`android-library-conventions`
    alias(libs.plugins.jetbrains.kotlinx.serialization)
}

dependencies {
    implementation(platform(libs.kotlinx.serialization.bom))
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.arsceneview)
}