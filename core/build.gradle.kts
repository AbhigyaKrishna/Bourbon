plugins {
    bourbon.`android-library-conventions`
    bourbon.`android-composable-conventions`
}

dependencies {
    implementation(libs.bundles.android)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.square.workflow)
}