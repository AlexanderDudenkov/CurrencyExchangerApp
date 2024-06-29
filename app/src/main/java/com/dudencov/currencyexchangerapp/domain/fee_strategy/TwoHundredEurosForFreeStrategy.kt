package com.dudencov.currencyexchangerapp.domain.fee_strategy

import com.dudencov.currencyexchangerapp.domain.UserBalance

class TwoHundredEurosForFreeStrategy(private val feeStrategy: FeeStrategy? = null) : FeeStrategy {

    override suspend fun getFee(sell: UserBalance): Double {
        val fee: Double? = feeStrategy?.getFee(sell)

        return when {
            fee != null && fee == 0.0 -> fee
            sell.currency == "EUR" && sell.amount == 200.0 -> 0.0
            else -> 0.007
        }
    }
}