package dev.sunnyday.postcreator.postcreator.operation

import android.content.Context
import android.net.Uri
import dev.sunnyday.postcreator.core.activityforresult.ActivityForResultRequest
import dev.sunnyday.postcreator.core.activityforresult.ActivityForResultRequestInteractor
import dev.sunnyday.postcreator.core.app.activityreqest.AppActivityForResultRequests.PickImage
import dev.sunnyday.postcreator.core.app.permissions.AppPermissionRequest
import dev.sunnyday.postcreator.core.permissions.PermissionRequestInteractor
import dev.sunnyday.postcreator.domain.backgrounds.BackgroundsRepository
import dev.sunnyday.postcreator.postcreator.R
import io.reactivex.Completable
import javax.inject.Inject

internal class AddBackgroundFromDeviceOperationImpl @Inject constructor(
    private val permissionsInteractor: PermissionRequestInteractor,
    private val activityInteractor: ActivityForResultRequestInteractor,
    private val backgroundsRepository: BackgroundsRepository,
    private val context: Context
) : AddBackgroundFromDeviceOperation {

    private val pickImageRequest: ActivityForResultRequest<Uri>
        get() = PickImage(context.getString(R.string.postcreator__prompt__add_background_title))

    override fun execute(): Completable =
        permissionsInteractor.requirePermission(AppPermissionRequest.Storage)
            .andThen(activityInteractor.startActivityForResult(pickImageRequest))
            .flatMapCompletable(backgroundsRepository::addBackground)

}