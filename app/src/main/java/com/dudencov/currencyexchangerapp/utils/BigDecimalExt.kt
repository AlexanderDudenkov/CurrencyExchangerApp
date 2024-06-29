package com.dudencov.currencyexchangerapp.utils

import java.math.BigDecimal
import java.math.RoundingMode

fun Double.roundToTwoDecimalPlaces(): BigDecimal =
    BigDecimal.valueOf(this).setScale(2, RoundingMode.HALF_UP)

fun String.roundToTwoDecimalPlaces(): BigDecimal =
    BigDecimal(this).setScale(2, RoundingMode.HALF_UP)
