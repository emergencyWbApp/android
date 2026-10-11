plugins {
    alias(libs.plugins.android.application)


}

android {
    namespace = "in.emergencyWbApp"
    compileSdk = 37


    defaultConfig {
        applicationId = "in.emergencyWbApp"
        minSdk = 24
        targetSdk = 36
        versionCode = 13
        versionName = "1.0.13"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
    dynamicFeatures += setOf(":essentials", ":premium")
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.feature.delivery.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)



    api(libs.androidx.collection)
    api(libs.androidx.collection.ktx)
    api(libs.androidx.work.runtime.ktx)
    api(libs.androidx.hilt.work)
}