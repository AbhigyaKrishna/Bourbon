plugins {
    bourbon.`kotlin-conventions`
    id(libs.plugins.android.application.get().pluginId)
    id(libs.plugins.jetbrains.kotlin.android.get().pluginId)
    id(libs.plugins.jetbrains.kotlin.kapt.get().pluginId)
    bourbon.`android-composable-conventions`
}

kapt {
    correctErrorTypes = true
    useBuildCache = true
    showProcessorStats = true
}

android {
    namespace = rootProject.group.toString()
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        applicationId = rootProject.group.toString()
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        multiDexEnabled = true
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
    implementation(project(":data"))
    implementation(libs.bundles.android)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.bundles.square.workflow)
    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.android.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.bundles.android.debug)
}