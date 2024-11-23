plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.coinvert"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.coinvert"
        minSdk = 24
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.room.common)
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // library to handle HTTP requests
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // library to handle HTTP requests
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.room:room-runtime:2.5.2") // Database library For Java
    annotationProcessor("androidx.room:room-compiler:2.5.2") // Database library For Java
    implementation("com.google.android.material:material:1.8.0") // or the latest version
}