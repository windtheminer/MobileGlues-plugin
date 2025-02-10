plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.fcl.plugin.mobileglues"
    compileSdk = 34

    ndkVersion = "27.0.12077973"

    defaultConfig {
        applicationId = "com.fcl.plugin.mobileglues"
        minSdk = 26
        targetSdk = 34
        versionCode = 1010
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters.clear()
            abiFilters.add("arm64-v8a")
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("../keystore.jks")
            storePassword = "666666"
            keyAlias = "MGKey"
            keyPassword = "666666"
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        configureEach {
            resValue("string","app_name","MobileGlues")

            manifestPlaceholders["des"] = "MobileGlues (OpenGL 4.0, 1.17+)"
            manifestPlaceholders["renderer"] = "MobileGlues:libmobileglues.so:libEGL.so"

            manifestPlaceholders["boatEnv"] = mutableMapOf<String,String>().apply {
                put("LIBGL_ES", "3")
                put("DLOPEN", "libspirv-cross-c-shared.so,libshaderconv.so")
            }.run {
                var env = ""
                forEach { (key, value) ->
                    env += "$key=$value:"
                }
                env.dropLast(1)
            }
            manifestPlaceholders["pojavEnv"] = mutableMapOf<String,String>().apply {
                put("LIBGL_ES", "3")
                put("DLOPEN", "libspirv-cross-c-shared.so,libshaderconv.so")
                put("POJAV_RENDERER", "opengles3")
            }.run {
                var env = ""
                forEach { (key, value) ->
                    env += "$key=$value:"
                }
                env.dropLast(1)
            }
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
}

dependencies {

    implementation(libs.gson)
    implementation(libs.androidx.activity.compose)
    implementation(project(":MobileGlues"))
}