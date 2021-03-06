package dev.sunnyday.postcreator.domain.backgrounds.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dev.sunnyday.postcreator.domain.backgrounds.BackgroundsRepository
import dev.sunnyday.postcreator.domain.backgrounds.BackgroundsRepositoryImpl
import dev.sunnyday.postcreator.domain.backgrounds.db.BackgroundsDao
import dev.sunnyday.postcreator.domain.backgrounds.db.BackgroundsDatabase
import dev.sunnyday.postcreator.domain.backgrounds.source.DefaultBackgroundsSource
import dev.sunnyday.postcreator.domain.backgrounds.source.DefaultBackgroundsSourceImpl
import dev.sunnyday.postcreator.domain.backgrounds.resolver.BackgroundResolver
import dev.sunnyday.postcreator.domain.backgrounds.resolver.BackgroundResolverImpl
import javax.inject.Singleton

@Module(includes = [DomainBackgroundsBindsModule::class])
class DomainBackgroundsModule {

    @Provides
    @Singleton
    internal fun provideDatabase(factory: BackgroundsDatabase.Factory): BackgroundsDatabase = factory.create()

    @Provides
    internal fun providesBackgroundsDao(database: BackgroundsDatabase): BackgroundsDao = database.backgroundsDao

}

@Module
internal interface DomainBackgroundsBindsModule {

    @Binds
    @Singleton
    fun bindBackgroundsRepository(impl: BackgroundsRepositoryImpl): BackgroundsRepository

    @Binds
    fun bindDefaultBackgroundsSource(impl: DefaultBackgroundsSourceImpl): DefaultBackgroundsSource

    @Binds
    fun bindBackgroundResolver(impl: BackgroundResolverImpl): BackgroundResolver

}