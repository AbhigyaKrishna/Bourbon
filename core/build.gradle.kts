plugins {
    bourbon.`android-library-conventions`
    bourbon.`android-composable-conventions`
}

dependencies {
    api(project(":domain"))
    api(project(":data"))
    api(libs.bundles.ballast)
    api(libs.arsceneview)
    testImplementation(libs.ballast.test)
}