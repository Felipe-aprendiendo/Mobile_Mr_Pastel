plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.grupo3.misterpastel"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.grupo3.misterpastel"
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

    /**
     * MUY IMPORTANTE para evitar errores en tests UI
     */
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    /**
     * NECESARIO para pruebas androidTest y test unitarios con recursos
     */
    testOptions {
        unitTests.isIncludeAndroidResources = true
        animationsDisabled = true
    }
}

dependencies {

    // ----------------------------
    // IMPLEMENTATION PRINCIPAL
    // ----------------------------
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Navegación
    implementation(libs.androidx.navigation.compose)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // ViewModel + LiveData
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Otros
    implementation(libs.androidx.window)
    implementation(libs.androidx.compose.material3.window.size)
    implementation(libs.coil.compose)
    implementation(libs.google.gson)

    // BCrypt
    implementation("org.mindrot:jbcrypt:0.4")

    // Retrofit y OkHttp
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // ----------------------------
    // UNIT TEST (test/)
    // ----------------------------
    testImplementation(libs.junit)

    // ----------------------------
    // ANDROID TEST (androidTest/)
    // ----------------------------

    // Esencial para tests UI con Compose
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // Espresso (lo usa Compose internamente)
    androidTestImplementation(libs.androidx.espresso.core)

    // JUnit4 para tests instrumentados
    androidTestImplementation(libs.junit4)

    // Navigation Testing (para simular NavController)
    androidTestImplementation(libs.navigation.testing)

    // BOM de Compose para pruebas
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // Manifest especial para test UI (solo en debug)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
