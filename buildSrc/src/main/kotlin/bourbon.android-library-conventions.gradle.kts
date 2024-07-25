plugins {
    com.android.library
    org.jetbrains.kotlin.android
    id("bourbon.kotlin-conventions")
    id("bourbon.kotlin-kapt-conventions")
}

android {
    namespace = "${rootProject.group}.${project.name}"
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

    lint {
        checkDependencies = true
        checkReleaseBuilds = true
        ignoreTestSources = true
        targetSdk = AppConfig.targetSdk
    }

    buildFeatures.viewBinding = true
}

dependencies {
    implementation(libs.findBundle("android").get())
}