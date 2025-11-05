plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.cs407.seefood"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cs407.seefood"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }

//        // Expose only ONCE (remove the duplicate you had)
//        buildConfigField(
//            "String",
//            "OPENAI_API_KEY",
//            "\"${project.findProperty("OPENAI_API_KEY") ?: ""}\""
//        )

        resValue(
            "string",
            "openai_api_key",
            project.findProperty("OPENAI_API_KEY") as String? ?: ""
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    // Compose (BOM keeps versions aligned)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Activity + Lifecycle/ViewModel in Compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Networking (Retrofit + OkHttp + Kotlinx Serialization)
    implementation(libs.retrofit.core)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit.kotlinx.serialization.converter)

    // Images
    implementation(libs.coil.compose)

    // CameraX (keep for later milestones; remove duplicates)
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)

    // ML Kit (optional now, OK on laptop without camera)
    implementation("com.google.mlkit:image-labeling:17.0.9")
    implementation("com.google.mlkit:object-detection:17.0.2")
    implementation("com.google.mlkit:image-labeling-custom:17.0.3")

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
