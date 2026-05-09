plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.koin.compiler)
}

android {
    namespace = Config.PACKAGE_NAME

    compileSdk {
        version = release(Config.COMPILE_SDK)
    }

    defaultConfig {
        applicationId = Config.PACKAGE_NAME
        minSdk = Config.MIN_SDK
        targetSdk = Config.TARGET_SDK
        versionCode = Config.VERSION_CODE
        versionName = Config.VERSION_NAME
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Jetpack Compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    debugImplementation(libs.compose.ui.tooling)
    // Splash screen
    implementation(libs.androidx.core.splashscreen)
    // DI
    implementation(platform(libs.koin.bom))
    implementation(libs.bundles.koin)
    // Navigation
    implementation(libs.bundles.decompose)
    implementation(libs.kotlinx.serialization)
    // Paging
    implementation(libs.bundles.paging)
    // Ktor
    implementation(libs.bundles.ktor)
    // Room
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)
    // DataStore
    implementation(libs.datastore)
}
