package com.dudencov.currencyexchangerapp.domain

import android.util.Log
import com.dudencov.currencyexchangerapp.data.Data
import com.dudencov.currencyexchangerapp.data.NetworkResult
import com.dudencov.currencyexchangerapp.data.Repository
import com.dudencov.currencyexchangerapp.di.IO
import com.dudencov.currencyexchangerapp.domain.fee_strategy.FeeStrategy
import com.dudencov.currencyexchangerapp.utils.AmountIsAbsent
import com.dudencov.currencyexchangerapp.utils.Constants.DEFAULT_USER_AMOUNT
import com.dudencov.currencyexchangerapp.utils.Constants.DEFAULT_USER_RECEIVE_CURRENCY
import com.dudencov.currencyexchangerapp.utils.Constants.DEFAULT_USER_SELL_CURRENCY
import com.dudencov.currencyexchangerapp.utils.roundToTwoDecimalPlaces
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class BalanceUseCaseImpl @Inject constructor(
    @IO
    private val ioDispatcher: CoroutineDispatcher,
    private val repository: Repository,
    private val feeStrategy: FeeStrategy,
) : BalanceUseCase {

    private val scope = CoroutineScope(ioDispatcher)
    private var data: Data = Data()

    private val _userBalances = MutableStateFlow<List<UserBalance>>(emptyList())
    override val userBalances: Flow<List<UserBalance>> = _userBalances.asStateFlow()

    private val _sellBalance = MutableStateFlow(
        UserBalance(
            currency = DEFAULT_USER_SELL_CURRENCY,
            amount = 0.0
        )
    )
    override val sellBalance: Flow<UserBalance> = _sellBalance.asStateFlow()

    private val _receiveBalance = MutableStateFlow(
        UserBalance(
            currency = DEFAULT_USER_RECEIVE_CURRENCY,
            amount = 0.0
        )
    )

    override val receiveBalance: Flow<UserBalance> = _receiveBalance.asStateFlow()

    private var isAppLaunchedAtFirst = true

    init {
        collectBalances()
        collectRates()
    }

    override suspend fun exchange(sell: UserBalance, receiveCurrency: String): ExchangeResult {
        if (!checkSellAmount(sell.amount)) throw AmountIsAbsent()

        repository.updateExchangeCounter(1)

        val fee = feeStrategy.getFee(sell)
        val amountFee = (sell.amount * fee).roundToTwoDecimalPlaces().toDouble()
        val totalSellAmount = sell.amount + amountFee

        repository.decreaseUserBalances(sell.copy(amount = totalSellAmount))

        val sellRate = data.currencyRates.find { it.currency == sell.currency }?.rate ?: 0.0

        val receiveRate = data.currencyRates.find { it.currency == receiveCurrency }?.rate ?: 0.0

        val receiveAmount = sell.amount * receiveRate / sellRate

        _sellBalance.emit(sell.copy(amount = 0.0))
        _receiveBalance.emit(UserBalance(currency = receiveCurrency, amount = receiveAmount))

        repository.increaseUserBalances(
            UserBalance(
                currency = receiveCurrency,
                amount = receiveAmount
            )
        )

        return ExchangeResult(
            sell = sell,
            receive = UserBalance(currency = receiveCurrency, amount = receiveAmount),
            fee = UserBalance(currency = sell.currency, amount = amountFee)
        )
    }

    private fun collectRates() {
        scope.launch {
            repository.currencyExchangeRates.collect { networkResult ->
                when (networkResult) {
                    is NetworkResult.Success -> {
                        data = networkResult.data
                    }

                    is NetworkResult.Error<*> -> {
                        Log.e(this::class.java.simpleName, (networkResult.errorMsg ?: ""))
                    }

                    is NetworkResult.Exception -> networkResult.e.printStackTrace()
                }
            }
        }
    }

    private fun collectBalances() {
        scope.launch {
            repository.userBalances.collect { list ->

                if (isAppLaunchedAtFirst && list.isNotEmpty()) {
                    isAppLaunchedAtFirst = false

                    repository.updateUserBalances(
                        UserBalance(
                            currency = data.baseCurrency,
                            amount = DEFAULT_USER_AMOUNT
                        )
                    )
                } else {
                    val sorted = list.sortedByDescending { it.amount }
                    _userBalances.emit(sorted)
                }
            }
        }
    }

    private fun checkSellAmount(amount: Double) = amount > 0
}