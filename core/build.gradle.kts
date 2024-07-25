plugins {
    bourbon.`android-library-conventions`
    bourbon.`android-composable-conventions`
}

dependencies {
    implementation(libs.bundles.ballast)
    testImplementation(libs.ballast.test)
}