plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("androidx.room")

}

android {
    namespace = "com.drpleaserespect.nyaamii"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.drpleaserespect.nyaamii"
        minSdk = 33
        targetSdk = 33
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
    buildFeatures {
        viewBinding = true
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }

}

dependencies {

    compileOnly("com.google.auto.value:auto-value-annotations:1.11.0")
    annotationProcessor("com.google.auto.value:auto-value:1.11.0")


    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    // Firebase Firestore
    implementation("com.google.firebase:firebase-firestore")
    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // Androidx Preference
    implementation("androidx.preference:preference-ktx:1.2.1")

    // Carousel
    implementation("org.imaginativeworld.whynotimagecarousel:whynotimagecarousel:2.1.0")
    implementation("me.relex:circleindicator:2.1.6")

    // Shimmering
    implementation("com.github.skydoves:androidveil:1.1.3")

    // Jetpack Room

    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-rxjava3:2.6.1")



    // RXJava3
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    implementation("io.reactivex.rxjava3:rxjava:3.1.8")



    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


}