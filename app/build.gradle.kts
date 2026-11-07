plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.gms.google-services")
}

fun envOrProp(key: String): String? =
    System.getenv(key)?.takeIf { it.isNotBlank() }
        ?: (project.findProperty(key) as? String)?.takeIf { it.isNotBlank() }

android {
    namespace = "com.bloodbridge.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bloodbridge.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        val isReleaseTask = gradle.startParameter.taskNames.any { it.contains("Release", ignoreCase = true) }
        val apiBaseProp = envOrProp("API_BASE_URL")
        val apiBase = if (!apiBaseProp.isNullOrBlank()) {
            apiBaseProp
        } else if (isReleaseTask) {
            throw GradleException(
                "API_BASE_URL must be specified for release builds. Never silently substitute production configuration with localhost."
            )
        } else {
            "http://10.0.2.2:5000/api/"
        }
        buildConfigField("String", "API_BASE_URL", "\"$apiBase\"")

        val cloudName = envOrProp("CLOUDINARY_CLOUD_NAME").orEmpty()
        val uploadPreset = envOrProp("CLOUDINARY_UPLOAD_PRESET").orEmpty()
        val webBase = (envOrProp("WEB_BASE_URL") ?: "https://blood-bridge.pages.dev").trimEnd('/')

        buildConfigField("String", "PRIVACY_URL", "\"$webBase/privacy-policy\"")
        buildConfigField("String", "TERMS_URL", "\"$webBase/terms\"")
        buildConfigField("String", "CLOUDINARY_CLOUD_NAME", "\"$cloudName\"")
        buildConfigField("String", "CLOUDINARY_UPLOAD_PRESET", "\"$uploadPreset\"")
    }

    signingConfigs {
        create("release") {
            val ksPath = envOrProp("ANDROID_KEYSTORE_PATH")
            if (!ksPath.isNullOrBlank()) {
                val ksFile = file(ksPath)
                if (ksFile.exists()) {
                    storeFile = ksFile
                    storePassword = envOrProp("ANDROID_KEYSTORE_PASSWORD")
                    keyAlias = envOrProp("ANDROID_KEY_ALIAS")
                    keyPassword = envOrProp("ANDROID_KEY_PASSWORD")
                } else {
                    throw GradleException("Keystore file specified at '$ksPath' does not exist.")
                }
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }

    lint {
        abortOnError = true
        checkReleaseBuilds = false
        textReport = true
        htmlReport = true
        xmlReport = true
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

gradle.taskGraph.whenReady {
    val isReleaseTask = allTasks.any { it.name in listOf("assembleRelease", "bundleRelease", "packageRelease") }
    if (isReleaseTask) {
        val required = listOf(
            "ANDROID_KEYSTORE_PATH",
            "ANDROID_KEYSTORE_PASSWORD",
            "ANDROID_KEY_ALIAS",
            "ANDROID_KEY_PASSWORD"
        )
        val missing = required.filter { envOrProp(it).isNullOrBlank() }
        if (missing.isNotEmpty()) {
            throw GradleException(
                "Production release builds require valid signing credentials. Missing: ${missing.joinToString(", ")}."
            )
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.10.01")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.navigation:navigation-compose:2.8.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")

    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-auth")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
