plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.shopping"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.shopping"
        minSdk = 25
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    implementation("net.sourceforge.jtds:jtds:1.3.1")
    implementation("com.google.firebase:firebase-firestore:24.10.0")
    implementation("com.google.firebase:firebase-analytics:21.5.0")
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
    testImplementation("junit:junit:4.13.2")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Dagger-hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")

    //corountines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    //view Pager 2
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    //Timber
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Moshi
    implementation("com.squareup.moshi:moshi-kotlin:1.13.0")

    //Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Retrofit with Moshi Converter
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    //stepView
    implementation("com.github.shuhart:stepview:1.5.1")

    //loading button
    implementation("com.github.leandroborgesferreira:loading-button-android:2.3.0")

    //circle image
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //validation
    implementation("com.wajahatkarim:easyvalidation-core:1.0.4")

    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    //rounded image
    implementation("com.makeramen:roundedimageview:2.3.0")

    //slide indicator
    implementation("me.relex:circleindicator:2.1.6")

    //paging3
    implementation("androidx.paging:paging-runtime-ktx:3.2.1")

    //shimmer skeleton
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    //read more
    implementation("kr.co.prnd:readmore-textview:1.0.0")

    //swipe down to reload
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")

    //spinner
    implementation("com.jaredrummler:material-spinner:1.3.1")

    //room
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    //zoom image
    implementation ("com.github.MikeOrtiz:TouchImageView:3.6")
}
