plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
   // id("dagger.hilt.android.plugin") // ← TAMBAH INI
   // kotlin("kapt") // ← TAMBAH INI
}

android {
    namespace = "com.example.gowatch"
    compileSdk = 36 // ← PERBAIKI: gunakan = 34, bukan version = release(36)

    defaultConfig {
        applicationId = "com.example.gowatch"
        minSdk = 24
        targetSdk = 36 // ← PERBAIKI: gunakan 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.compose.animation:animation-core:1.6.0")
    // Hilt
   // implementation("com.google.dagger:hilt-android:2.48")
   // kapt("com.google.dagger:hilt-compiler:2.48")
   // implementation("androidx.hilt:hilt-navigation-compose:1.0.0") // ← TAMBAH INI

    // Lain-lain
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")

    // NETWORKING - Retrofit & OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Jetpack Compose
    implementation("androidx.compose.ui:ui:1.5.4")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.3")

    // ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    // Coil untuk Jetpack Compose
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Kotlin coroutines untuk network
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
    implementation("androidx.compose.material3:material3:1.1.2")

    // Versi libs
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}