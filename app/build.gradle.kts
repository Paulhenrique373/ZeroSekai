plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")

    // Firebase
    id("com.google.gms.google-services")
}

android {

    namespace = "com.example.zerosekai"

    compileSdk = 34

    defaultConfig {

        applicationId = "com.example.zerosekai"

        minSdk = 24

        targetSdk = 34

        versionCode = 1

        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        release {

            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {

        sourceCompatibility =
            JavaVersion.VERSION_17

        targetCompatibility =
            JavaVersion.VERSION_17
    }

    kotlinOptions {

        jvmTarget = "17"
    }

    buildFeatures {

        compose = true
    }
}

dependencies {

    // Core
    implementation(libs.androidx.core.ktx)

    implementation(
        libs.androidx.lifecycle.runtime.ktx
    )

    implementation(
        libs.androidx.activity.compose
    )

    // Compose BOM
    implementation(
        platform(libs.androidx.compose.bom)
    )

    implementation(libs.androidx.ui)

    implementation(
        libs.androidx.ui.graphics
    )

    implementation(
        libs.androidx.ui.tooling.preview
    )

    implementation(
        libs.androidx.material3
    )

    // Material Icons
    implementation(
        "androidx.compose.material:material-icons-extended"
    )

    // Coil
    implementation(
        "io.coil-kt:coil-compose:2.6.0"
    )

    // 🔥 SUPABASE
    implementation(
        "io.github.jan-tennert.supabase:postgrest-kt:2.5.4"
    )

    implementation(
        "io.github.jan-tennert.supabase:storage-kt:2.5.4"
    )

    implementation(
        "io.github.jan-tennert.supabase:gotrue-kt:2.5.4"
    )

    implementation(
        "io.ktor:ktor-client-android:2.3.12"
    )

    // Navigation
    implementation(
        "androidx.navigation:navigation-compose:2.8.0"
    )

    // ViewModel
    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4"
    )

    // Coroutines
    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1"
    )

    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1"
    )

    // Firebase BOM
    implementation(
        platform(
            "com.google.firebase:firebase-bom:33.10.0"
        )
    )

    // Firebase Auth
    implementation(
        "com.google.firebase:firebase-auth-ktx"
    )

    // Firestore
    implementation(
        "com.google.firebase:firebase-firestore-ktx"
    )

    // Storage
    implementation(
        "com.google.firebase:firebase-storage-ktx"
    )

    // Analytics
    implementation(
        "com.google.firebase:firebase-analytics"
    )

    // Tests
    testImplementation(
        libs.junit
    )

    androidTestImplementation(
        libs.androidx.junit
    )

    androidTestImplementation(
        libs.androidx.espresso.core
    )

    androidTestImplementation(
        platform(libs.androidx.compose.bom)
    )

    androidTestImplementation(
        libs.androidx.ui.test.junit4
    )

    debugImplementation(
        libs.androidx.ui.tooling
    )

    debugImplementation(
        libs.androidx.ui.test.manifest
    )
}