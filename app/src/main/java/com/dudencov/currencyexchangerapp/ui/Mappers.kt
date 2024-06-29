package com.dudencov.currencyexchangerapp.ui

import com.dudencov.currencyexchangerapp.domain.ExchangeResult
import com.dudencov.currencyexchangerapp.domain.UserBalance
import com.dudencov.currencyexchangerapp.ui.MainViewModel.Balance
import com.dudencov.currencyexchangerapp.ui.MainViewModel.DialogState
import com.dudencov.currencyexchangerapp.ui.MainViewModel.SellBalance
import com.dudencov.currencyexchangerapp.utils.roundToTwoDecimalPlaces

fun ExchangeResult.toDialogState() = DialogState(
    sell = this.sell.toBalance(),
    receive = this.receive.toBalance(),
    fee = this.fee.toBalance()
)

fun UserBalance.toBalance() =
    Balance(
        currency = currency,
        amount = amount.roundToTwoDecimalPlaces()
    )

fun SellBalance.toUserBalance() =
    UserBalance(currency = currency, amount = amount.toDouble())