package dev.sunnyday.postcreator.postcreator.provider

import androidx.core.content.FileProvider

internal class PostShareProvider : FileProvider() {

    companion object {

        const val AUTHORITY = "dev.sunnyday.postcreator.share.provider"

    }

}