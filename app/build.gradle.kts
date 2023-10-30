plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.testapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.testapplication"
        minSdk = 33
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    buildToolsVersion = "34.0.0"
    sourceSets {
        getByName("main") {
            res {
                srcDirs(
                    "src\\main\\res",
                    "src\\main\\res\\layouts\\home",
                    "src\\main\\res\\layouts\\gallery",
                    "src\\main\\res\\layouts\\gallery\\galleryitems",
                    "src\\main\\res\\layouts\\main",
                    "src\\main\\res\\layouts\\slideshow", "src\\main\\res", "src\\main\\res\\drawables\\main",
                    "src\\main\\res",
                    "src\\main\\res\\drawables\\gallery", "src\\main\\res", "src\\main\\res\\drawables\\gallery\\spinswipe"
                )
            }
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    implementation("com.google.android.material:material:1.11.0-alpha03")

    // Material Design 3
    implementation("androidx.compose.material3:material3:1.2.0-alpha09")
    // Add material icons
    implementation("androidx.compose.material:material-icons-core")
    // Add full set of material icons
    implementation("androidx.compose.material:material-icons-extended")
    // Add window size utils
    implementation("androidx.compose.material3:material3-window-size-class")
    // Add the main APIs for the underlying toolkit systems - measurement/layout
    implementation("androidx.compose.ui:ui")

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.1")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    // Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    // Integration with activities
    implementation("androidx.activity:activity-compose:1.8.0")

    implementation("androidx.navigation:navigation-fragment-ktx:2.7.4")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.4")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}