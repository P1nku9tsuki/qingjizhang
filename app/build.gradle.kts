plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.3.10"
}

android {
    namespace = "com.natsuki.qingjizhang"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.natsuki.qingjizhang"
        minSdk = 31
        targetSdk = 36
        versionCode = 2
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            splits {
                abi {
                    isEnable = true
                    reset()
                    include("arm64-v8a", "armeabi-v7a")
                    isUniversalApk = false
                }
            }
        }


    buildTypes {
        buildTypes {
            release {
                isMinifyEnabled = true
                isShrinkResources = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
            debug {
                isMinifyEnabled = false
            }
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }

        buildFeatures {
            compose = true
        }

        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
                excludes += "/META-INF/DEPENDENCIES"
                excludes += "/META-INF/LICENSE"
                excludes += "/META-INF/LICENSE.txt"
                excludes += "/META-INF/NOTICE"
                excludes += "/META-INF/NOTICE.txt"
                excludes += "/META-INF/*.kotlin_module"
                excludes += "/DebugProbesKt.bin"
            }
        }
        dependencies {
            implementation(platform(libs.androidx.compose.bom))
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.compose.material3)
            implementation(libs.androidx.compose.ui)
            implementation(libs.androidx.compose.ui.graphics)
            implementation(libs.androidx.compose.ui.tooling.preview)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.lifecycle.runtime.ktx)
            testImplementation(libs.junit)
            androidTestImplementation(platform(libs.androidx.compose.bom))
            androidTestImplementation(libs.androidx.compose.ui.test.junit4)
            androidTestImplementation(libs.androidx.espresso.core)
            androidTestImplementation(libs.androidx.junit)
            debugImplementation(libs.androidx.compose.ui.test.manifest)
            debugImplementation(libs.androidx.compose.ui.tooling)
            implementation("top.yukonga.miuix.kmp:miuix-ui:0.9.2")
            implementation("top.yukonga.miuix.kmp:miuix-icons:0.9.2")
            implementation("top.yukonga.miuix.kmp:miuix-preference:0.9.2")
            //implementation("top.yukonga.miuix.kmp:miuix-blur:0.9.2")
            //implementation("io.github.fletchmckee.liquid:liquid:1.1.1")
            val roomVersion = "2.7.0-alpha11"
            implementation("androidx.room:room-runtime:$roomVersion")
            implementation("androidx.room:room-ktx:$roomVersion")
            ksp("androidx.room:room-compiler:$roomVersion")
            implementation("androidx.datastore:datastore-preferences:1.1.1")
            implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
            implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
            implementation("androidx.compose.material:material-icons-extended:1.7.0")
            implementation("dev.chrisbanes.haze:haze:1.6.10")
        }
    }
}





