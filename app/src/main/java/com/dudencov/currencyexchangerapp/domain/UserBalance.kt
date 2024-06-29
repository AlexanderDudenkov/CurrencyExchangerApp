package com.dudencov.currencyexchangerapp.domain

data class UserBalance(
    val currency: String = "",
    val amount: Double = 0.0
)

data class ExchangeResult(
    val sell: UserBalance,
    val receive: UserBalance,
    val fee: UserBalance
)