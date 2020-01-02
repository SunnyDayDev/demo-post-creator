plugins {
    id("com.android.library")
    id("kotlin-android")
    kotlin("kapt")
}

android {
    compileSdkVersion(Android.targetSdk)
    buildToolsVersion(Android.buildToolVersion)

    defaultConfig {
        minSdkVersion(Android.minSdk)
        targetSdkVersion(Android.targetSdk)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    Rx.addTo(this)
    Dagger.addTo(this, useAndroid = true, useAsist = false, enableProcessing = false)

    implementation(project(PostCreator.Core.common))
    implementation(project(PostCreator.Core.permissions))
    implementation(project(PostCreator.Core.activityForResult))

    testImplementation(Test.junit)
    androidTestImplementation(Test.junitExt)
    androidTestImplementation(Test.espresso)
}
