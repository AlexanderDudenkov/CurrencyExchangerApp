package com.dudencov.currencyexchangerapp.data

import android.util.Log
import com.dudencov.currencyexchangerapp.di.IO
import com.dudencov.currencyexchangerapp.domain.UserBalance
import com.dudencov.currencyexchangerapp.utils.NotEnoughBalance
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    @IO
    private val ioDispatcher: CoroutineDispatcher,
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : Repository {

    private val _currencyExchangeRates =
        MutableSharedFlow<NetworkResult<Data>>(extraBufferCapacity = 1)

    override val currencyExchangeRates: Flow<NetworkResult<Data>> =
        _currencyExchangeRates.asSharedFlow()

    override val userBalances: Flow<List<UserBalance>> = localDataSource.userBalances

    override val exchangeCounterFlow: Flow<Int> = localDataSource.exchangeCounterFlow

    private var scope = CoroutineScope(ioDispatcher)

    override fun loadData() {
        if (!scope.isActive) scope = CoroutineScope(ioDispatcher)

        scope.launch {
            while (scope.isActive) {
                Log.d(this::class.java.simpleName, "loadData")
                val res = remoteDataSource.getData()
                _currencyExchangeRates.emit(res)

                if (isUserBalancesCreated()) createUserBalances(res)

                delay(5000)
            }
        }
    }

    override fun cancelLoadingData() {
        scope.cancel()
    }

    override suspend fun updateUserBalances(userBalance: UserBalance) {
        localDataSource.updateUserBalances(userBalance)
    }

    override suspend fun updateUserBalances(userBalances: List<UserBalance>) {
        localDataSource.updateUserBalances(userBalances)
    }

    override suspend fun increaseUserBalances(increase: UserBalance) {
        val usersAmount =
            (userBalances.first().find { it.currency == increase.currency }?.amount ?: 0.0)
        val newUsersAmount = usersAmount + increase.amount

        updateUserBalances(increase.copy(amount = newUsersAmount))
    }

    override suspend fun decreaseUserBalances(decrease: UserBalance) {
        val usersAmount =
            (userBalances.first().find { it.currency == decrease.currency }?.amount ?: 0.0)
        val newUsersAmount = usersAmount - decrease.amount

        if (newUsersAmount < 0.0) throw NotEnoughBalance()

        updateUserBalances(decrease.copy(amount = newUsersAmount))
    }

    override suspend fun updateExchangeCounter(value: Int) {
        localDataSource.updateExchangeCounter(value)
    }

    private suspend fun isUserBalancesCreated() = userBalances.first().isEmpty()

    private suspend fun createUserBalances(result: NetworkResult<Data>) {
        localDataSource.updateUserBalances(
            result.getOrNull()
                ?.currencyRates?.map { UserBalance(currency = it.currency) }
                ?: emptyList()
        )
    }
}