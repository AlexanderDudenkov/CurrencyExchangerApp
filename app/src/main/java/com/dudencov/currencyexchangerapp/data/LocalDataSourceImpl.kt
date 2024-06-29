package com.dudencov.currencyexchangerapp.data

import com.dudencov.currencyexchangerapp.di.Default
import com.dudencov.currencyexchangerapp.domain.UserBalance
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    @Default
    private val dispatcher: CoroutineDispatcher,
) : LocalDataSource {

    private var exchangeCounter = 0

    private var scope = CoroutineScope(dispatcher)

    private var userBalancesMap = HashMap<String, UserBalance>()

    private val _userBalances =
        MutableSharedFlow<HashMap<String, UserBalance>>(replay = 1)

    override val userBalances: Flow<List<UserBalance>> = _userBalances.map { it.values.toList() }

    private val _exchangeCounterFlow = MutableStateFlow(exchangeCounter)

    override val exchangeCounterFlow: Flow<Int> = _exchangeCounterFlow.asStateFlow()

    init {
        /*
        * val exchangeCounter = get value from DB
        * _exchangeCounterFlow.emit(exchangeCounter)
        * */
        scope.launch {
            _userBalances.emit(userBalancesMap)
        }
    }

    override suspend fun updateUserBalances(userBalance: UserBalance) {
        userBalancesMap[userBalance.currency] = userBalance
        _userBalances.emit(userBalancesMap)
    }

    override suspend fun updateUserBalances(userBalances: List<UserBalance>) {
        userBalancesMap = (userBalances.associateBy { it.currency } as LinkedHashMap)
        _userBalances.emit(userBalancesMap)
    }

    override suspend fun updateExchangeCounter(value: Int) {
        /*
        * save exchangeCounter to DB
        * */
        exchangeCounter += value
        _exchangeCounterFlow.emit(exchangeCounter)
    }
}