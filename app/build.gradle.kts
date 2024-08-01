import java.util.Properties





plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.unit_3.sogong_test"
    compileSdk = 34

    splits {
        abi {
            isEnable = true
            reset()
            include ("armeabi-v7a", "x86","arm64-v8a")
        }
    }

    defaultConfig {
        applicationId = "com.unit_3.sogong_test"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 프로퍼티를 선언하고, 저장한 키값을 불러온다.
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())

        buildConfigField("String", "Naver_Client_ID", properties.getProperty("naver_client_id") ?: "\"\"")
        buildConfigField("String", "Naver_Client_Secret", properties.getProperty("naver_client_secret") ?: "\"\"")
        buildConfigField("String", "Naver_Clova_Client_ID", properties.getProperty("naver_clova_client_id") ?: "\"\"")
        buildConfigField("String", "Naver_Clova_Client_Secret", properties.getProperty("naver_clova_client_secret") ?: "\"\"")
        buildConfigField("String", "OPENAI_API_KEY", properties.getProperty("OPENAI_API_KEY") ?: "\"\"")
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
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

    dataBinding {
        enable = true
    }
}

dependencies {
    implementation("com.airbnb.android:lottie:4.2.0")
//    implementation("app.rive:rive-android:0.10.0")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.navigation:navigation-runtime-ktx:2.7.7")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.firebase:firebase-database-ktx:21.0.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.firebase:firebase-firestore-ktx:25.0.0")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.media3:media3-common:1.4.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.naver.maps:map-sdk:3.18.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("net.sourceforge.jexcelapi:jxl:2.6.12")
    implementation("com.google.firebase:firebase-storage-ktx:20.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
