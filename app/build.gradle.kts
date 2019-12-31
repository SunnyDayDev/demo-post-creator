plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    kotlin("kapt")
}

android {
    compileSdkVersion(Android.targetSdk)
    buildToolsVersion(Android.buildToolVersion)
    
    defaultConfig {
        applicationId = "dev.sunnyday.postcreator"
        minSdkVersion(Android.minSdk)
        targetSdkVersion(Android.targetSdk)
        versionCode = 1
        versionName = "1.0"
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    
    buildTypes {
        named("release"){
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    
}

dependencies {
    implementation(Kotlin.stdlibJdk8)
    //implementation(AndroidX.appCompat)
    //implementation(AndroidX.coreKtx)
    //implementation(AndroidX.constraintLayout)
    //implementation(AndroidX.exifInterface)

    Dagger.addTo(this, useAndroid = true)
    Rx.addTo(this)

    implementation(project(PostCreator.Core.common))
    implementation(project(PostCreator.Core.ui))
    implementation(project(PostCreator.Core.permissions))
    implementation(project(PostCreator.Core.app))
    implementation(project(PostCreator.Feature.postCreator))
    //implementation(project(PostCreator.Feature.postCreatorBoard))
    //implementation(project(PostCreator.Feature.backgroundSwitcher))
    //implementation(project(PostCreator.Feature.stickersBoard))

    testImplementation(Test.junit)
    androidTestImplementation(Test.junitExt)
    androidTestImplementation(Test.espresso)
}
