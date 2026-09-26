plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.neveriaventa.pos"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.neveriaventa.pos"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }
}
