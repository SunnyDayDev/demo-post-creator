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

    Dagger.addTo(this, useAndroid = true)
    Rx.addTo(this)
    Room.addTo(this)

    implementation(project(PostCreator.Core.common))
    implementation(project(PostCreator.Core.ui))
    implementation(project(PostCreator.Core.permissions))
    implementation(project(PostCreator.Core.activityForResult))
    implementation(project(PostCreator.Core.app))
    implementation(project(PostCreator.Domain.backgrounds))
    implementation(project(PostCreator.Feature.postCreator))
    implementation(project(PostCreator.Feature.drawableChooser))

    testImplementation(Test.junit)
    androidTestImplementation(Test.junitExt)
    androidTestImplementation(Test.espresso)
}
