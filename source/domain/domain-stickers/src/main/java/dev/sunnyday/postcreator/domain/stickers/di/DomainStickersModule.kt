package dev.sunnyday.postcreator.domain.stickers.di

import dagger.Binds
import dagger.Module
import dev.sunnyday.postcreator.domain.stickers.StickersRepository
import dev.sunnyday.postcreator.domain.stickers.StickersRepositoryImpl
import javax.inject.Singleton

@Module(includes = [InternalStickersModule::class])
class DomainStickersModule

@Module
internal interface InternalStickersModule {

    @Binds
    @Singleton
    fun bindStickersRepository(impl: StickersRepositoryImpl): StickersRepository

}