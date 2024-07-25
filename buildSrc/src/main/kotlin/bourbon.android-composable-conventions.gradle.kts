import com.android.build.gradle.BaseExtension

plugins {
    org.jetbrains.kotlin.plugin.compose
}

composeCompiler {
    enableStrongSkippingMode = true
    includeSourceInformation = true
}

project.extensions.getByName<BaseExtension>("android").buildFeatures.compose = true