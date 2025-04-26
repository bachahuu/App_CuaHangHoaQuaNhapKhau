plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.duan_appbanhang"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.duan_appbanhang"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
repositories{
    google()
    mavenCentral()
}
dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.activity:activity-ktx:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // Thêm thư viện đúng cách
    implementation("com.makeramen:roundedimageview:2.3.0")
    //glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // Volley (Thêm vào đây)
    implementation("com.android.volley:volley:1.2.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0") // Đồng bộ phiên bản
    // Đảm bảo tất cả các thư viện Kotlin sử dụng cùng một phiên bản
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.22"))
    //RxJava
    implementation ("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation ("io.reactivex.rxjava3:rxjava:3.0.0")
    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.github.akarnokd:rxjava3-retrofit-adapter:3.0.0")
}
