plugins {
    com.google.devtools.ksp
}

dependencies {
    implementation(platform(libs.findLibrary("arrow.bom").get()))
    implementation(libs.findBundle("arrow").get())
    ksp(libs.findLibrary("arrow.optics.ksp").get())
}