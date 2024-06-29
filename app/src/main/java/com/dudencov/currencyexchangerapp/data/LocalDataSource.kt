package com.dudencov.currencyexchangerapp.data

import com.dudencov.currencyexchangerapp.domain.UserBalance
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    val userBalances: Flow<List<UserBalance>>

    val exchangeCounterFlow: Flow<Int>

    suspend fun updateUserBalances(userBalance: UserBalance)

    suspend fun updateUserBalances(userBalances: List<UserBalance>)

    suspend fun updateExchangeCounter(value: Int)
}