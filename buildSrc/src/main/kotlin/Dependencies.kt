@file:Suppress("MemberVisibilityCanBePrivate")

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

}

object Test {

    const val junit = "junit:junit:4.12"
    const val junitExt = "androidx.test.ext:junit:1.1.1"
    const val espresso = "androidx.test.espresso:espresso-core:3.2.0"

}

object PostCreator {

    object Core {

        const val common = ":core:core-common"

    }

    object Feature {

        const val postCreator = ":feature:feature-postcreator"
        const val backgroundSwitcher = ":feature:feature-backgroundswitcher"

    }

}