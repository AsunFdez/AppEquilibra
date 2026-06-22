plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
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
            applicationIdSuffix = ".release"
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
    implementation("androidx.concurrent:concurrent-futures:1.2.0")
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

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    //Room para SQLite
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation("com.google.android.gms:play-services-auth:21.6.0")

    // Credential Manager
    implementation("androidx.credentials:credentials:1.6.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.6.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
}

// Tarea de Javadoc corregida para KTS
tasks.register<Javadoc>("generateJavadoc") {
    description = "Genera la documentación Javadoc del proyecto."
    group = "documentation"

    // Acceso a la extensión de Android de forma segura para KTS
    val android = project.extensions.getByName("android") as com.android.build.gradle.AppExtension
    
    // Configurar fuentes
    source(android.sourceSets.getByName("main").java.srcDirs)
    
    // El classpath inicial incluye el SDK de Android
    classpath = project.files(android.bootClasspath)

    // Configuración de opciones de Javadoc
    options {
        this as StandardJavadocDocletOptions
        encoding = "UTF-8"
        charSet = "UTF-8"
        memberLevel = JavadocMemberLevel.PROTECTED
        links("https://developer.android.com/reference", "https://docs.oracle.com/javase/8/docs/api/")
        addStringOption("Xdoclint:none", "-quiet")
    }

    // Lugar de salida
    destinationDir = file("C:/Users/asunf/Documentos/DAM/Proyecto/AppEquilibra/Documentación/JavaDoc")

    // Añadir dependencias al classpath justo antes de ejecutar para asegurar que estén resueltas
    doFirst {
        val debugVariant = android.applicationVariants.find { it.name == "debug" }
        if (debugVariant != null) {
            classpath += debugVariant.getCompileClasspath(null)
            // Añadir clases compiladas para resolver R y DataBinding
            classpath += debugVariant.javaCompileProvider.get().outputs.files
        }
    }
}
