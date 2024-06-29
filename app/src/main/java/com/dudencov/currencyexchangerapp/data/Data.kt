package com.dudencov.currencyexchangerapp.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(

    @SerialName("base")
    val baseCurrency: String = "",

    @SerialName("date")
    val date: String = "",

    @Serializable(with = RateListSerializer::class)
    @SerialName("rates")
    val currencyRates: List<CurrencyRate> = emptyList()
)

const val currencySerialName = "currency"
const val rateSerialName = "rate"

@Serializable
data class CurrencyRate(
    @SerialName(currencySerialName)
    val currency: String,

    @SerialName(rateSerialName)
    val rate: Double
)
