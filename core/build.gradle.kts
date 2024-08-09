plugins {
    bourbon.`android-library-conventions`
    bourbon.`android-composable-conventions`
}

dependencies {
    api(project(":domain"))
    api(project(":data"))
    api(libs.bundles.ballast)
    api(libs.arsceneview)
    implementation(libs.markdown.text)
    testImplementation(libs.ballast.test)
}