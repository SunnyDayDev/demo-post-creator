package dev.sunnyday.postcreator.postcreator.di

import androidx.lifecycle.ViewModel
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.sunnyday.postcreator.core.app.dagger.ViewModelKey
import dev.sunnyday.postcreator.postcreator.di.factory.ViewModelAssistedFactory
import dev.sunnyday.postcreator.postcreator.operation.ChooseStickerOperation
import dev.sunnyday.postcreator.postcreator.operation.ChooseStickerOperationImpl
import dev.sunnyday.postcreator.postcreator.operation.AddBackgroundFromDeviceOperation
import dev.sunnyday.postcreator.postcreator.operation.AddBackgroundFromDeviceOperationImpl
import dev.sunnyday.postcreator.postcreator.operation.SavePostOperation
import dev.sunnyday.postcreator.postcreator.operation.SavePostOperationImpl
import dev.sunnyday.postcreator.postcreator.styles.FixedInMemoryTextStylesSource
import dev.sunnyday.postcreator.postcreator.styles.TextStylesSource
import dev.sunnyday.postcreator.postcreator.viewModel.PostCreatorViewModel

@Module(includes = [InternalPostCreatorModule::class])
class PostCreatorModule

@AssistedModule
@Module(includes = [AssistedInject_InternalPostCreatorModule::class])
internal interface InternalPostCreatorModule {

    @Binds
    fun bindDrawViewToFileOperation(impl: SavePostOperationImpl): SavePostOperation

    @Binds
    fun bindAddBackgroundFromDeviceOperation(impl: AddBackgroundFromDeviceOperationImpl): AddBackgroundFromDeviceOperation

    @Binds
    fun bindStickersBoardInteractor(impl: ChooseStickerOperationImpl): ChooseStickerOperation

    @Binds
    fun bindTextStylesSource(impl: FixedInMemoryTextStylesSource): TextStylesSource

    @Binds
    @IntoMap
    @ViewModelKey(PostCreatorViewModel::class)
    fun bindFactory(impl: PostCreatorViewModel.Factory): ViewModelAssistedFactory<out ViewModel>

}