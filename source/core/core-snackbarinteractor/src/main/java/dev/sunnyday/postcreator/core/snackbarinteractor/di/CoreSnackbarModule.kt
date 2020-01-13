package dev.sunnyday.postcreator.core.snackbarinteractor.di

import dagger.Binds
import dagger.Module
import dev.sunnyday.postcreator.core.snackbarinteractor.SnackbarInteractor
import dev.sunnyday.postcreator.core.snackbarinteractor.SnackbarInteractorImpl

@Module(includes = [InternalCoreSnackbarModule::class])
class CoreSnackbarModule

@Module
internal interface InternalCoreSnackbarModule {

    @Binds
    fun bindSnackbarInteractor(impl: SnackbarInteractorImpl): SnackbarInteractor

}