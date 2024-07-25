import com.android.build.gradle.BaseExtension

plugins {
    org.jetbrains.kotlin.plugin.compose
}

composeCompiler {
    enableStrongSkippingMode = true
    includeSourceInformation = true
}

project.extensions.getByName<BaseExtension>("android").buildFeatures.compose = true

dependencies {
    add("implementation", platform(libs.findLibrary("androidx.compose.bom").get()))
    add("implementation", libs.findBundle("android.compose").get())
    add("androidTestImplementation", platform(libs.findLibrary("androidx.compose.bom").get()))
    add("androidTestImplementation", libs.findLibrary("androidx.ui.test.junit4").get())
    add("debugImplementation", libs.findBundle("android.compose.debug").get())
}