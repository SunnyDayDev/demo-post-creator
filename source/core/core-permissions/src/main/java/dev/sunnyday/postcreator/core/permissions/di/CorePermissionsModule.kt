package dev.sunnyday.postcreator.core.permissions.di

import dagger.Binds
import dagger.Module
import dev.sunnyday.postcreator.core.permissions.PermissionRequestInteractor
import dev.sunnyday.postcreator.core.permissions.PermissionRequestInteractorImpl

@Module(includes = [InternalPermissionsModule::class])
interface CorePermissionsModule

@Module
internal interface InternalPermissionsModule {

    @Binds
    fun bindPermissionsInteractor(impl: PermissionRequestInteractorImpl): PermissionRequestInteractor

}