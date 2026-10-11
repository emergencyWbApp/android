plugins {
    alias(libs.plugins.android.dynamic.feature)

    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)


}
android {
    namespace = "in.emergencyWbApp.essentials"
    compileSdk = 37

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }


}

dependencies {
    // ═══════════════════════════════════════════════════
    // 1. Base App Reference
    // ═══════════════════════════════════════════════════
    implementation(project(":app"))
    implementation(libs.androidx.compose.foundation)

    // ═══════════════════════════════════════════════════
    // 2. Core AndroidX
    // ═══════════════════════════════════════════════════
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.collection)
    implementation(libs.androidx.collection.ktx)

    // ═══════════════════════════════════════════════════
    // 3. Compose
    // ═══════════════════════════════════════════════════
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // ═══════════════════════════════════════════════════
    // 4. Lifecycle
    // ═══════════════════════════════════════════════════
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // ═══════════════════════════════════════════════════
    // 5. Navigation
    // ═══════════════════════════════════════════════════
    implementation(libs.androidx.navigation.compose)

    // ═══════════════════════════════════════════════════
    // 6. Hilt (DI) — 🔑 MUST HAVE ksp compiler
    // ═══════════════════════════════════════════════════
    implementation(libs.androidx.hilt.navigation.compose)

    // ═══════════════════════════════════════════════════
    // 7. Retrofit + OkHttp + Gson
    // ═══════════════════════════════════════════════════
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)

    // ═══════════════════════════════════════════════════
    // 8. Room 3.x
    // ═══════════════════════════════════════════════════
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)


            // ═══════════════════════════════════════════════════
    // 9. Coroutines
    // ═══════════════════════════════════════════════════
    implementation(libs.kotlinx.coroutines.android)

    // ═══════════════════════════════════════════════════
    // 10. WorkManager
    // ═══════════════════════════════════════════════════


    // ═══════════════════════════════════════════════════
    // 11. Play Feature Delivery
    // ═══════════════════════════════════════════════════
    implementation(libs.feature.delivery.ktx)

    // ═══════════════════════════════════════════════════
    // 12. Testing
    // ═══════════════════════════════════════════════════
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

}