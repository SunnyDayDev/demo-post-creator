package dev.sunnyday.postcreator.postcreator.di

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Binds
import dagger.Module
import dev.sunnyday.postcreator.postcreator.saver.ViewAsImageSaver
import dev.sunnyday.postcreator.postcreator.saver.ViewAsImageSaverImpl

@AssistedModule
@Module(includes = [
    AssistedInject_PostCreatorModule::class,
    InternalPostCreatorModule::class
])
class PostCreatorModule

@Module
internal interface InternalPostCreatorModule {

    @Binds
    fun bindViewAsImageSaver(impl: ViewAsImageSaverImpl): ViewAsImageSaver

}