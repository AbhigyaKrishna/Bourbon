plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.kapt)
    alias(libs.plugins.jetbrains.kotlin.compose.compiler)
}

kapt {
    correctErrorTypes = true
    useBuildCache = true
    showProcessorStats = true
}

composeCompiler {
    enableStrongSkippingMode = true
    includeSourceInformation = true
}

android {
    namespace = rootProject.group.toString()
    compileSdk = 34

    defaultConfig {
        applicationId = rootProject.group.toString()
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = rootProject.version.toString()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            versionNameSuffix = "-dev"
            applicationIdSuffix = ".debug"
        }

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
        compose = true
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
    implementation(libs.bundles.android)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.bundles.square.workflow)
    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.android.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.bundles.android.debug)
}