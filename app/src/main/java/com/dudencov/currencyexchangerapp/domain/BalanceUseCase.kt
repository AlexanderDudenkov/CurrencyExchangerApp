package com.dudencov.currencyexchangerapp.domain

import kotlinx.coroutines.flow.Flow

interface BalanceUseCase {

    val userBalances: Flow<List<UserBalance>>

    val sellBalance: Flow<UserBalance>

    val receiveBalance: Flow<UserBalance>

    suspend fun exchange(sell: UserBalance, receiveCurrency: String): ExchangeResult
}