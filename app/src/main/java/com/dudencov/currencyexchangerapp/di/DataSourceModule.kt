package com.dudencov.currencyexchangerapp.di

import com.dudencov.currencyexchangerapp.data.LocalDataSource
import com.dudencov.currencyexchangerapp.data.LocalDataSourceImpl
import com.dudencov.currencyexchangerapp.data.RemoteDataSource
import com.dudencov.currencyexchangerapp.data.RemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataSourceModule {

    @Binds
    @Singleton
    fun bindRemoteDataSource(value: RemoteDataSourceImpl): RemoteDataSource

    @Binds
    @Singleton
    fun bindLocalDataSource(value: LocalDataSourceImpl): LocalDataSource
}