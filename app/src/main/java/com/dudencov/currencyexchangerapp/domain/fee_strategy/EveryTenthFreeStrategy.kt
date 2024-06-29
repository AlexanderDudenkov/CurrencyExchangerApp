package com.dudencov.currencyexchangerapp.domain.fee_strategy

import com.dudencov.currencyexchangerapp.domain.UserBalance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class EveryTenthFreeStrategy @Inject constructor(
    private val exchangeCounterFlow: Flow<Int>,
    private val feeStrategy: FeeStrategy? = null,
) : FeeStrategy {

    override suspend fun getFee(sell: UserBalance): Double {
        val exchangeCounter = exchangeCounterFlow.first()
        val fee: Double? = feeStrategy?.getFee(sell)

        return when {
            fee != null && fee == 0.0 -> fee
            exchangeCounter % 10 == 0 -> 0.0
            else -> 0.007
        }
    }
}