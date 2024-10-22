plugins {
    org.jetbrains.kotlin.kapt
}

kapt {
    correctErrorTypes = true
    useBuildCache = true
    showProcessorStats = true
}

dependencies {
    implementation(libs.findLibrary("koin").get())
}