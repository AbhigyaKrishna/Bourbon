plugins {
    com.android.library
    org.jetbrains.kotlin.android
    id("bourbon.kotlin-conventions")
    id("bourbon.di-conventions")
//    id("bourbon.arrow-conventions")
}

android {
    namespace = "${rootProject.group}.${project.name}"
    compileSdk = AppConfig.COMPILE_SDK

    defaultConfig {
        minSdk = AppConfig.MIN_SDK
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
        targetSdk = AppConfig.TARGET_SDK
    }

    buildFeatures.viewBinding = true
}

dependencies {
    implementation(libs.findBundle("android").get())
}