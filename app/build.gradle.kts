plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.dicodingdevelopercoachingmlkit"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.dicodingdevelopercoachingmlkit"
        minSdk = 21
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Use this dependency to bundle the model with your app
    implementation("com.google.mlkit:face-detection:16.1.6")
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("com.google.mlkit:translate:17.0.2")

    implementation("androidx.camera:camera-core:1.3.3")
    implementation("androidx.camera:camera-camera2:1.3.3")
    implementation("androidx.camera:camera-lifecycle:1.3.3")
    implementation("androidx.camera:camera-video:1.3.3")

    implementation("androidx.camera:camera-view:1.3.3")
    implementation("androidx.camera:camera-extensions:1.3.3")

    // Concurrent library for asynchronous coroutines
    implementation("androidx.concurrent:concurrent-futures-ktx:1.1.0")
    
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}