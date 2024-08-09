import java.util.Properties

plugins {
    bourbon.`kotlin-conventions`
    id(libs.plugins.android.application.get().pluginId)
    id(libs.plugins.jetbrains.kotlin.android.get().pluginId)
    bourbon.`di-conventions`
    bourbon.`android-composable-conventions`
    alias(libs.plugins.google.services)
    alias(libs.plugins.google.secrets.gradle.plugin)
}

group = rootProject.group

android {
    signingConfigs {
        val keyStoreProperties = Properties().apply {
            load(rootDir.resolve("key.properties").reader())
        }
        getByName("debug") {
            keyAlias = keyStoreProperties.getProperty("keyAlias")
            keyPassword = keyStoreProperties.getProperty("keyPassword")
            storeFile = file(keyStoreProperties.getProperty("storeFile"))
            storePassword = keyStoreProperties.getProperty("storePassword")
        }
    }
    namespace = group.toString()
    compileSdk = AppConfig.COMPILE_SDK

    defaultConfig {
        applicationId = rootProject.group.toString()
        minSdk = AppConfig.MIN_SDK
        targetSdk = AppConfig.TARGET_SDK
        versionCode = AppConfig.VERSION_CODE
        versionName = AppConfig.VERSION_NAME

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        multiDexEnabled = true
        signingConfig = signingConfigs.getByName("debug")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    buildFeatures {
        buildConfig = true
    }

    lint {
        checkDependencies = true
        checkReleaseBuilds = true
        ignoreTestSources = true
    }

    packaging {
        resources {
            excludes.addAll(setOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "META-INF/*.version",
                "META-INF/proguard/*",
                "/*.properties",
                "fabric/*.properties",
                "META-INF/*.properties",
                "META-INF/*.kotlin_module"
            ))
        }
    }
}

dependencies {
    implementation(project(":core"))
    implementation(libs.bundles.android)
    annotationProcessor(libs.glide.compiler)
    implementation(libs.firebase.ui.storage)
    implementation(libs.koin)
    implementation(libs.generative.ai)
    testImplementation(libs.junit)
    testImplementation(libs.ballast.test)
    androidTestImplementation(libs.bundles.android.test)
}