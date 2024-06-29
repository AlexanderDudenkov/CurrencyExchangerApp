package com.dudencov.currencyexchangerapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SerializationModule {

    @Singleton
    @Provides
    fun provideDefaultJson(): Json = Json
}