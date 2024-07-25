plugins {
    com.android.library
    org.jetbrains.kotlin.android
    id("bourbon.kotlin-conventions")
    id("bourbon.kotlin-kapt-conventions")
}

android {
    namespace = rootProject.group.toString()
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        minSdk = AppConfig.minSdk
        vectorDrawables.useSupportLibrary = true
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    buildFeatures.viewBinding = true
}

dependencies {
    implementation(libs.findBundle("android").get())
}