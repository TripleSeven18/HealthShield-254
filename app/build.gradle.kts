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
    //Firebase
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.androidx.camera.core)
    implementation(libs.play.services.location)
    implementation(libs.vision.common)
    implementation(libs.play.services.mlkit.text.recognition.common)
    //Firebase End
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Navigation

    implementation("androidx.navigation:navigation-runtime-ktx:2.9.4")
    implementation("androidx.navigation:navigation-compose:2.9.4")

    //Nav.End

    //Lottie Dependency

    implementation("com.airbnb.android:lottie-compose:4.2.0")

    //End of Lottie

    implementation("io.coil-kt:coil-compose:2.4.0")



}