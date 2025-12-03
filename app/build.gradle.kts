plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.fabiobassi.famigliab"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.fabiobassi.famigliab"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(libs.androidx.compose.ui.text.google.fonts)

    // ✅ Use only one Compose BOM (it handles all Compose versions)
    implementation(platform(libs.androidx.compose.bom))

    // 🎨 Jetpack Compose + Material 3
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // 🧩 Activity + Lifecycle Compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // 🧭 Navigation (optional but useful)
    implementation(libs.androidx.navigation.compose)
}