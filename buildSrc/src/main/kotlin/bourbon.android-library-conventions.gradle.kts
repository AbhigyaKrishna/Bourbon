import java.util.Properties

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

    val props = Properties().apply {
        load(rootDir.resolve("key.properties").reader())
    }

    signingConfigs {
        getByName("debug") {
            keyAlias = props.getProperty("keyAlias")
            keyPassword = props.getProperty("keyPassword")
            storeFile = file(props.getProperty("storeFile"))
            storePassword = props.getProperty("storePassword")
        }
    }

    defaultConfig {
        minSdk = AppConfig.MIN_SDK
        vectorDrawables.useSupportLibrary = true
        signingConfig = signingConfigs.getByName("debug")
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
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