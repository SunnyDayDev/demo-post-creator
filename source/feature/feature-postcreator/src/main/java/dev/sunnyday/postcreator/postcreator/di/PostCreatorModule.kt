package dev.sunnyday.postcreator.postcreator.di

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Binds
import dagger.Module
import dev.sunnyday.postcreator.postcreator.operation.AddBackgroundFromDeviceOperation
import dev.sunnyday.postcreator.postcreator.operation.AddBackgroundFromDeviceOperationImpl
import dev.sunnyday.postcreator.postcreator.operation.DrawViewToFileOperation
import dev.sunnyday.postcreator.postcreator.operation.DrawViewToFileOperationImpl
import dev.sunnyday.postcreator.postcreator.styles.FixedInMemoryTextStylesSource
import dev.sunnyday.postcreator.postcreator.styles.TextStylesSource

@AssistedModule
@Module(includes = [
    AssistedInject_PostCreatorModule::class,
    InternalPostCreatorModule::class
])
class PostCreatorModule

@Module
internal interface InternalPostCreatorModule {

    @Binds
    fun bindDrawViewToFileOperation(impl: DrawViewToFileOperationImpl): DrawViewToFileOperation

    @Binds
    fun bindAddBackgroundFromDeviceOperation(impl: AddBackgroundFromDeviceOperationImpl): AddBackgroundFromDeviceOperation

    @Binds
    fun bindTextStylesSource(impl: FixedInMemoryTextStylesSource): TextStylesSource

}