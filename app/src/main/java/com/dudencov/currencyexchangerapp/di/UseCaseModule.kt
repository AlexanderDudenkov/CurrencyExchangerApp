package com.dudencov.currencyexchangerapp.di

import com.dudencov.currencyexchangerapp.domain.BalanceUseCase
import com.dudencov.currencyexchangerapp.domain.BalanceUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface UseCaseModule {

    @Binds
    @Singleton
    fun bindBalanceUseCase(value: BalanceUseCaseImpl): BalanceUseCase
}