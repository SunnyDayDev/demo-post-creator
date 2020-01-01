package dev.sunnyday.postcreator.domain.backgrounds.prefs

import android.content.Context
import dev.sunnyday.postcreator.core.app.rx.AppSchedulers
import dev.sunnyday.postcreator.domain.backgrounds.db.BackgroundsDao
import javax.inject.Inject

internal class BackgroundsRepositoryPrefsImpl @Inject constructor(
    private val context: Context
) : BackgroundsRepositoryPrefs {

    private val prefs get() = context.getSharedPreferences(
        "dev.sunnyday.postcreator.domain.backgrounds.prefs",
        Context.MODE_PRIVATE)

    override var isInitialized: Boolean
        get() = prefs.getBoolean(KEY_IS_INITIALIZED, false)
        set(value) {
            prefs.edit()
                .putBoolean(KEY_IS_INITIALIZED, value)
                .apply()
        }

    companion object {

        private const val KEY_IS_INITIALIZED =
            "dev.sunnyday.postcreator.domain.backgrounds.prefs.isInitialized"

    }

}