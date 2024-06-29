package com.dudencov.currencyexchangerapp.di

import com.dudencov.currencyexchangerapp.data.Repository
import com.dudencov.currencyexchangerapp.data.RepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindRepository(value: RepositoryImpl): Repository
}