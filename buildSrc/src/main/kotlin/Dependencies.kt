@file:Suppress("MemberVisibilityCanBePrivate")

import org.gradle.kotlin.dsl.DependencyHandlerScope

object Jvm {
    const val version = "1.8"
}

object Kotlin {
    const val version = "1.3.61"
    const val stdlibJdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
}

object Android {

    const val buildToolClassPath = "com.android.tools.build:gradle:3.6.0-rc01"
    const val buildToolVersion = "29.0.2"

    const val targetSdk = 29
    const val minSdk = 21

}

object AndroidX {

    const val appCompat = "androidx.appcompat:appcompat:1.1.0"
    const val coreKtx = "androidx.core:core-ktx:1.1.0"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:1.1.3"
    const val material = "com.google.android.material:material:1.1.0-rc01"
    const val recyclerView = "androidx.recyclerview:recyclerview:1.1.0"
    const val card = "androidx.cardview:cardview:1.0.0"
    const val exifInterface = "androidx.exifinterface:exifinterface:1.2.0-beta01"
    const val fragment = "androidx.fragment:fragment:1.2.0-rc04"
    const val fragmentKtx = "androidx.fragment:fragment-ktx:1.2.0-rc04"

}

object Rx {

    const val java = "io.reactivex.rxjava2:rxjava:2.2.16"
    const val android = "io.reactivex.rxjava2:rxandroid:2.1.1"
    const val kotlin = "io.reactivex.rxjava2:rxkotlin:2.4.0"

    fun addTo(handler: DependencyHandlerScope) {
        handler.add("implementation", java)
        handler.add("implementation", android)
        handler.add("implementation", kotlin)
    }

}

object Dagger {

    private const val version = "2.25.4"

    const val api = "com.google.dagger:dagger:$version"
    const val android = "com.google.dagger:dagger-android:$version"
    const val androidSupport = "com.google.dagger:dagger-android-support:$version"
    const val androidProcessor = "com.google.dagger:dagger-android-processor:$version"
    const val compiler = "com.google.dagger:dagger-compiler:$version"

    object AssistedInject {

        private const val version = "0.5.2"

        const val api = "com.squareup.inject:assisted-inject-annotations-dagger2:$version"
        const val processor = "com.squareup.inject:assisted-inject-processor-dagger2:$version"

    }

    fun addTo(handler: DependencyHandlerScope,
              useAndroid: Boolean = false,
              useAsist: Boolean = true,
              enableProcessing: Boolean = true) {
        handler.add("implementation", api)
        if (enableProcessing) {
            handler.add("kapt", compiler)
        }

        if (useAndroid) {
            handler.add("implementation", android)
            handler.add("implementation", androidSupport)
            if (enableProcessing) {
                handler.add("kapt", androidProcessor)
            }
        }

        if (useAsist) {
            handler.add("compileOnly", AssistedInject.api)
            if (enableProcessing) {
                handler.add("kapt", AssistedInject.processor)
            }
        }
    }

}

object Room {

    private const val version = "2.2.3"

    const val api = "androidx.room:room-runtime:$version"
    const val ktx = "androidx.room:room-ktx:$version"
    const val rx = "androidx.room:room-rxjava2:$version"
    const val processor = "androidx.room:room-compiler:$version"

    fun addTo(handler: DependencyHandlerScope) {
        handler.add("implementation", api)
        handler.add("implementation", ktx)
        handler.add("implementation", rx)
        handler.add("kapt", processor)
    }

}

object Glide {

    private const val version = "4.10.0"

    const val api = "com.github.bumptech.glide:glide:$version"
    const val compiler = "com.github.bumptech.glide:compiler:$version"

}

object Log {

    const val timber = "com.jakewharton.timber:timber:4.7.1"

}

object Test {

    const val junit = "junit:junit:4.12"
    const val junitExt = "androidx.test.ext:junit:1.1.1"
    const val espresso = "androidx.test.espresso:espresso-core:3.2.0"

}

object PostCreator {

    object Core {

        const val common = ":core:core-common"
        const val dagger = ":core:core-dagger"
        const val ui = ":core:core-ui"
        const val permissions = ":core:core-permissions"
        const val app = ":core:core-app"

    }

    object Domain {

        const val backgrounds = ":domain:domain-backgrounds"

    }

    object Feature {

        const val postCreator = ":feature:feature-postcreator"
        const val postCreatorBoard = ":feature:feature-postcreatorboard"
        const val drawableChooser = ":feature:feature-drawablechooser"
        const val stickersBoard = ":feature:feature-stickersboard"

    }

}