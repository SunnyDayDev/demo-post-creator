package dev.sunnyday.postcreator

import android.os.Bundle
import androidx.fragment.app.commit
import dagger.android.support.DaggerAppCompatActivity
import dev.sunnyday.postcreator.postcreator.PostCreatorFragment

internal class AppActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app__activity)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.contentRoot, PostCreatorFragment())
            }
        }
    }

}