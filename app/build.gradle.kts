plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // ✅ Tambahkan plugin Compose Compiler. Ini WAJIB ada.
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.tbimaan"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tbimaan"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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

    // ✅ buildFeatures { compose = true } WAJIB ADA untuk mengaktifkan Compose.
    buildFeatures {
        compose = true
    }

    // ✅ composeOptions juga diperlukan untuk menentukan versi compiler.
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1" // Sesuaikan dengan versi Kotlin/Compose Anda
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Ketergantungan Inti AndroidX
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.04.01"))

    // Ketergantungan UI Jetpack Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Ketergantungan Navigasi Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Ketergantungan Jaringan (Retrofit) - Duplikat sudah dihapus, ini benar.
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Ketergantungan Coil untuk memuat gambar
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Ketergantungan Pengujian
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.04.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Ketergantungan Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:34.7.0"))
    implementation("com.google.firebase:firebase-analytics")

    //firebase fcm
    implementation("com.google.firebase:firebase-messaging")

}
