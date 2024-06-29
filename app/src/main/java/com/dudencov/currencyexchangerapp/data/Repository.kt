package com.dudencov.currencyexchangerapp.data

import com.dudencov.currencyexchangerapp.domain.UserBalance
import kotlinx.coroutines.flow.Flow

interface Repository {

    val currencyExchangeRates: Flow<NetworkResult<Data>>

    val userBalances: Flow<List<UserBalance>>

    val exchangeCounterFlow: Flow<Int>

    fun loadData()

    fun cancelLoadingData()

    suspend fun updateUserBalances(userBalance: UserBalance)

    suspend fun updateUserBalances(userBalances: List<UserBalance>)

    suspend fun increaseUserBalances(increase: UserBalance)

    suspend fun decreaseUserBalances(decrease: UserBalance)

    suspend fun updateExchangeCounter(value: Int)
}