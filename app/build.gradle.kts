plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "in.emergencyWbApp"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "in.emergencyWbApp"
        minSdk = 24
        targetSdk = 37
        versionCode = 7
        versionName = "1.0.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    dynamicFeatures += setOf(":dashboard")
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.feature.delivery.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)


}