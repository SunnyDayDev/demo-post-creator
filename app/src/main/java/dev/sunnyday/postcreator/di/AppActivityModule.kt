package dev.sunnyday.postcreator.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.sunnyday.postcreator.core.app.dagger.PerFeature
import dev.sunnyday.postcreator.postcreator.PostCreatorFragment
import dev.sunnyday.postcreator.postcreator.di.PostCreatorModule

@Module
internal interface AppActivityModule {

    @PerFeature
    @ContributesAndroidInjector(modules = [PostCreatorModule::class])
    fun contributePostCreatorFragment(): PostCreatorFragment

}