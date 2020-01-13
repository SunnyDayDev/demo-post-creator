package dev.sunnyday.postcreator.core.dialoginteractor.di

import dagger.Binds
import dagger.Module
import dev.sunnyday.postcreator.core.dialoginteractor.DialogInteractor
import dev.sunnyday.postcreator.core.dialoginteractor.DialogInteractorImpl
import javax.inject.Singleton

@Module(includes = [InternalCoreDialogInteractorModule::class])
class CoreDialogInteractorModule

@Module
internal interface InternalCoreDialogInteractorModule {

    @Binds
    @Singleton
    fun bindDialogInteractor(impl: DialogInteractorImpl): DialogInteractor

}