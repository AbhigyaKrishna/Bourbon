plugins {
    com.google.devtools.ksp
}

dependencies {
    add("implementation", platform(libs.findLibrary("arrow.bom").get()))
    add("implementation", libs.findBundle("arrow").get())
    ksp(libs.findLibrary("arrow.optics.ksp").get())
}