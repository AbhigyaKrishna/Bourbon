// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id(libs.plugins.android.application.get().pluginId) apply false
    id(libs.plugins.android.library.get().pluginId) apply false
    id(libs.plugins.jetbrains.kotlin.android.get().pluginId) apply false
    id(libs.plugins.jetbrains.kotlin.kapt.get().pluginId) apply false
}

group = "me.abhigya.bourbon"
version = "1.0"

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}