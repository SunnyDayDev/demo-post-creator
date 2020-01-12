package dev.sunnyday.postcreator.postcreator.operation

import android.content.Context
import android.net.Uri
import dev.sunnyday.postcreator.core.activityforresult.ActivityRequestInteractor
import dev.sunnyday.postcreator.core.app.activityreqest.AppActivityRequests.CropImage
import dev.sunnyday.postcreator.core.app.activityreqest.AppActivityRequests.PickImage
import dev.sunnyday.postcreator.core.app.permissions.AppPermissionRequest
import dev.sunnyday.postcreator.core.permissions.PermissionRequestInteractor
import dev.sunnyday.postcreator.domain.backgrounds.BackgroundsRepository
import dev.sunnyday.postcreator.postcreator.R
import io.reactivex.Completable
import io.reactivex.Maybe
import javax.inject.Inject

internal class AddBackgroundFromDeviceOperationImpl @Inject constructor(
    private val permissionsInteractor: PermissionRequestInteractor,
    private val activityInteractor: ActivityRequestInteractor,
    private val backgroundsRepository: BackgroundsRepository,
    private val context: Context
) : AddBackgroundFromDeviceOperation {

    override fun execute(): Completable =
        permissionsInteractor.requirePermission(AppPermissionRequest.Storage)
            .andThen(pickImage())
            .flatMap(this::cropImage)
            .flatMapCompletable(backgroundsRepository::addBackground)

    private fun pickImage(): Maybe<Uri> {
        val chooserTitle = context.getString(R.string.postcreator__prompt__add_background_title)
        val request = PickImage(chooserTitle)
        return activityInteractor.startActivityForResult(request)
    }

    private fun cropImage(imageUri: Uri): Maybe<Uri> {
        val request = CropImage(imageUri, widthToHeightRatio = 360f / 328f)
        return activityInteractor.startActivityForResult(request)
    }

}