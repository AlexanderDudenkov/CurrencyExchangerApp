package com.dudencov.currencyexchangerapp.di

import com.dudencov.currencyexchangerapp.data.LocalDataSource
import com.dudencov.currencyexchangerapp.domain.fee_strategy.EveryTenthFreeStrategy
import com.dudencov.currencyexchangerapp.domain.fee_strategy.FeeStrategy
import com.dudencov.currencyexchangerapp.domain.fee_strategy.FirstFiveFreeStrategy
import com.dudencov.currencyexchangerapp.domain.fee_strategy.TwoHundredEurosForFreeStrategy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FeeStrategyModule {

    @Provides
    @Singleton
    fun providesFirstFiveFreeStrategy(localDataSource: LocalDataSource): FeeStrategy {
        val counter = localDataSource.exchangeCounterFlow

        val everyTenthFreeStrategy = EveryTenthFreeStrategy(exchangeCounterFlow = counter)
        val twoHundredEurosForFreeStrategy = TwoHundredEurosForFreeStrategy(everyTenthFreeStrategy)
        return FirstFiveFreeStrategy(
            exchangeCounterFlow = counter,
            feeStrategy = twoHundredEurosForFreeStrategy
        )
    }
}