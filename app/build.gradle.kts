plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.triple7.healthshield254"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.triple7.healthshield254"
        minSdk = 23
        targetSdk = 36
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation
    implementation("androidx.navigation:navigation-runtime-ktx:2.7.7")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Lottie Animation
    implementation("com.airbnb.android:lottie-compose:6.4.0")

    // Coil Image Loading
    implementation("io.coil-kt:coil-compose:2.6.0")

    // CameraX core libraries
    implementation("androidx.camera:camera-core:1.3.4")
    implementation("androidx.camera:camera-camera2:1.3.4")
    implementation("androidx.camera:camera-lifecycle:1.3.4")
    implementation("androidx.camera:camera-view:1.3.4")

// ML Kit Barcode Scanning
    implementation("com.google.mlkit:barcode-scanning:17.3.0")

// Guava (required for ProcessCameraProvider)
    implementation("com.google.guava:guava:31.1-android")

    // ML Kit
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation(libs.vision.common)
    implementation(libs.play.services.mlkit.text.recognition.common)
    implementation(libs.play.services.mlkit.text.recognition)
    implementation(libs.play.services.location)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    // Google Maps for Compose
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.maps.android:maps-compose:4.3.3")
    implementation("com.google.maps.android:maps-compose-utils:4.3.3")
    implementation("com.google.maps.android:maps-compose-widgets:4.3.3")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)




    // Cloudinary
    implementation("io.coil-kt:coil-compose:2.0.0") // Load and display images in Jetpack Compose
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Make API requests (network client)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Convert JSON to Kotlin objects and vice versa
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3") // Log API request and response for debugging
    implementation("com.cloudinary:cloudinary-android:2.3.1") // Cloudinary SDK for uploading and managing images
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3") // Coroutines support for background tasks (API calls, uploads)
    // End

}
