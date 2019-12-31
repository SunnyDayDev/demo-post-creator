package dev.sunnyday.postcreator.postcreator

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import dagger.android.support.DaggerFragment
import dev.sunnyday.postcreator.backgroundswitcher.Background
import dev.sunnyday.postcreator.backgroundswitcher.BackgroundSwitcherToolbarListener
import dev.sunnyday.postcreator.core.app.rx.AppSchedulers
import dev.sunnyday.postcreator.core.common.android.Dimen
import dev.sunnyday.postcreator.core.permissions.AppPermissionRequest
import dev.sunnyday.postcreator.core.permissions.PermissionRequestInteractor
import dev.sunnyday.postcreator.stickersboard.Sticker
import dev.sunnyday.postcreator.stickersboard.StickersBoard
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.postcreator__fragment.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.min

class PostCreatorFragment : DaggerFragment() {

    @Inject
    internal lateinit var permissionsInteractor: PermissionRequestInteractor

    @Inject
    internal lateinit var schedulers: AppSchedulers

    private val dispose = CompositeDisposable()

    private val textStyleSwitcher: TextStyleSwitcher by lazy {
        TextStyleSwitcher(context!!)
    }

    override fun onStart() {
        super.onStart()

        requestStoragePermissions()
    }

    override fun onDestroy() {
        super.onDestroy()
        dispose.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.postcreator__fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateCreatorSize()
        setupTextStyleSwitcher()
        setupBackgroundSwitcher()
        setupStickers()
    }

    private fun updateCreatorSize() {
        val display = resources.displayMetrics
        val smallestSize = min(display.widthPixels, display.heightPixels)

        creatorView.updateLayoutParams {
            width = smallestSize
        }
    }

    private fun setupTextStyleSwitcher() {
        textStyleSwitcher.applyNextStyle(creatorView)

        switchTextStyleButton.setOnClickListener {
            textStyleSwitcher.applyNextStyle(creatorView)
        }
    }

    private fun setupBackgroundSwitcher() {
        backgroundSwitcher.items = listOf(
            Background.Color(Color.WHITE),
            Background.Gradient(0xFF00EFC8.toInt(), 0xFF0762E5.toInt()),
            Background.Gradient(0xFFAAE400.toInt(), 0xFF04B025.toInt()),
            Background.Gradient(0xFFFFBA00.toInt(), 0xFFFF590B.toInt()),
            Background.Gradient(0xFFFF003F.toInt(), 0xFF99004F.toInt()),
            Background.Resource(R.drawable.bg_beach),
            Background.Resource(R.drawable.bg_stars_center),
            Background.Color(Color.BLACK),
            Background.Gradient(0xFF02EFC8.toInt(), 0xFF0762E5.toInt()),
            Background.Gradient(0xFFAAE460.toInt(), 0xFF04B025.toInt()),
            Background.Gradient(0xFFFFBA00.toInt(), 0xFF27590B.toInt()),
            Background.Gradient(0xFF4F0004.toInt(), 0xFF29404F.toInt()))

        backgroundSwitcher.addListener(object : BackgroundSwitcherToolbarListener {

            override fun onBackgroundSelected(background: Background) {
                val context = context ?: return

                creatorView.background = Background.getDrawable(context, background)

                if (background is Background.Color && background.color == Color.WHITE) {
                    val actionsBorderWidth = Dimen.dp(2, context).toInt()
                    creatorView.setActionsBorderWidth(actionsBorderWidth)
                } else {
                    creatorView.setActionsBorderWidth(0)
                }
            }

        })

        backgroundSwitcher.selectedPosition = 0
    }

    private fun setupStickers() {
        stickersButton.setOnClickListener {
            val context = context ?: return@setOnClickListener

            val stickers = (1L..24L).map {
                Sticker(it, Uri.parse("file:///android_asset/stickers/$it.png"))
            }

            StickersBoard.show(context, stickers) {
                creatorView.addImage(it.uri)
            }
        }
    }

    private fun requestStoragePermissions() {
        permissionsInteractor.requirePermission(AppPermissionRequest.Storage)
            .observeOn(schedulers.ui)
            .subscribeBy(
                onComplete = { Timber.d("Storage permissions granged") },
                onError = { Timber.d("Error: $it")  }
            )
            .let(dispose::add)
    }

}
