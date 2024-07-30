plugins {
    bourbon.`android-library-conventions`
    bourbon.`android-composable-conventions`
}

dependencies {
    api(project(":domain"))
    api(project(":data"))
    api(libs.bundles.ballast)
    testImplementation(libs.ballast.test)
}