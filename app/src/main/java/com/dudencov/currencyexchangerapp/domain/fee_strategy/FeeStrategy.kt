package com.dudencov.currencyexchangerapp.domain.fee_strategy

import com.dudencov.currencyexchangerapp.domain.UserBalance

interface FeeStrategy {

    suspend fun getFee(sell: UserBalance): Double
}