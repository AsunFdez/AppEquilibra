plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "asunfdiez.com.equilibra"
    compileSdk = 36

    defaultConfig {
        applicationId = "asunfdiez.com.equilibra"
        minSdk = 26
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
            applicationIdSuffix = "1"
            versionNameSuffix = "1.1.0"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    // AndroidX Core
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.activity)
    implementation(libs.recyclerview)
    implementation(libs.ext.junit)
    implementation(libs.work.runtime)
    testImplementation(libs.junit)
    testImplementation(libs.arch.core.testing)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.cardview)

    implementation(libs.material.v1110)
    implementation(libs.gson)

    // AGREGAR LIFECYCLE (LiveData + ViewModel)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.common.java8)
    implementation(libs.lifecycle.runtime)

    // MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    //Room para SQLite
    implementation("androidx.room:room-runtime:2.8.4")
    annotationProcessor("androidx.room:room-compiler:2.8.4")


}