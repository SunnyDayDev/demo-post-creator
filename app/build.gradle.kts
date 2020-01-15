import org.gradle.internal.impldep.org.codehaus.plexus.util.PropertyUtils.loadProperties
import java.util.Properties

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
        versionCode = 4
        versionName = "1.1.1"
        
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

    signingConfigs {
        create("release") {
            try {
                val properties = Properties()
                properties.load(file("signing.properties").inputStream())

                storeFile = project.file(properties["keystore.path"]!!)
                storePassword = properties["keystore.password"] as String
                keyAlias = properties["key.alias"] as String
                keyPassword = properties["key.password"] as String
            } catch (e: Throwable) {
                println("Error: $e")
                return@create
            }
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

dependencies {
    implementation(Kotlin.stdlibJdk8)

    Dagger.addTo(this, useAndroid = true)
    Rx.addTo(this)
    Room.addTo(this)

    implementation(project(PostCreator.Core.common))
    implementation(project(PostCreator.Core.activityTracker))
    implementation(project(PostCreator.Core.permissions))
    implementation(project(PostCreator.Core.activityForResult))
    implementation(project(PostCreator.Core.snackbarInteractor))
    implementation(project(PostCreator.Core.dialogInteractor))
    implementation(project(PostCreator.Core.app))
    implementation(project(PostCreator.Domain.backgrounds))
    implementation(project(PostCreator.Domain.stickers))
    implementation(project(PostCreator.Feature.postCreator))
    implementation(project(PostCreator.Feature.drawableChooser))
    implementation(project(PostCreator.Feature.stickersBoard))

    testImplementation(Test.junit)
    androidTestImplementation(Test.junitExt)
    androidTestImplementation(Test.espresso)
}
