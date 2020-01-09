package dev.sunnyday.postcreator.postcreator.di

import dagger.Binds
import dagger.Module
import dev.sunnyday.postcreator.postcreator.operation.AddBackgroundFromDeviceOperation
import dev.sunnyday.postcreator.postcreator.operation.AddBackgroundFromDeviceOperationImpl
import dev.sunnyday.postcreator.postcreator.operation.DrawViewToFileOperation
import dev.sunnyday.postcreator.postcreator.operation.DrawViewToFileOperationImpl
import dev.sunnyday.postcreator.postcreator.styles.FixedInMemoryTextStylesSource
import dev.sunnyday.postcreator.postcreator.styles.TextStylesSource

@Module(includes = [InternalPostCreatorModule::class])
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