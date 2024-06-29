package com.dudencov.currencyexchangerapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dudencov.currencyexchangerapp.data.Repository
import com.dudencov.currencyexchangerapp.domain.BalanceUseCase
import com.dudencov.currencyexchangerapp.domain.ExchangeResult
import com.dudencov.currencyexchangerapp.utils.Constants.DEFAULT_USER_RECEIVE_CURRENCY
import com.dudencov.currencyexchangerapp.utils.roundToTwoDecimalPlaces
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val balanceUseCase: BalanceUseCase,
    private val repository: Repository
) : ViewModel(), DefaultLifecycleObserver {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effects = Channel<SideEffects>()
    val effects: Flow<SideEffects> = _effects.receiveAsFlow()

    init {
        collectBalances()
        collectSellBalance()
        collectReceiveBalance()
    }

    fun onSellCurrencySelected(currency: String) {
        _state.update { old ->
            old.copy(sell = old.sell.copy(currency = currency))
                .apply { sell.updateAmount(old.sell.amount) }
        }
    }

    fun onReceiveCurrencySelected(currency: String) {
        _state.update { state ->
            state.copy(
                receive = state.receive.copy(
                    currency = currency
                )
            )
        }
    }

    fun onSubmit() {
        viewModelScope.launch {
            var info: ExchangeResult? = null

            runCatching {
                info = balanceUseCase.exchange(
                    sell = state.value.sell.toUserBalance(),
                    receiveCurrency = state.value.receive.currency
                )
            }.onSuccess {
                info?.let {
                    _state.update { state ->
                        state.copy(dialogState = it.toDialogState())
                    }
                    _effects.send(SideEffects.ShowDialog)
                }
            }.onFailure { it.printStackTrace() }
        }
    }

    fun onDone() {
        viewModelScope.launch { _effects.send(SideEffects.Default) }
        _state.update { state ->
            state.copy(
                sell = state.sell.apply { updateAmount("0.00") },
                receive = state.receive.copy(amount = "0.00".roundToTwoDecimalPlaces())
            )
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        repository.loadData()
    }

    override fun onStop(owner: LifecycleOwner){
        repository.cancelLoadingData()
    }

    private fun collectBalances() {
        viewModelScope.launch {
            balanceUseCase.userBalances.collect { userBalances ->
                _state.update { state ->
                    state.copy(
                        currencies = userBalances.map { it.currency },
                        balances = userBalances.map { it.toBalance() }
                    )
                }
            }
        }
    }

    private fun collectSellBalance() {
        viewModelScope.launch {
            balanceUseCase.sellBalance.collect { userBalance ->
                _state.update { state ->
                    state.copy(sell = state.sell.copy(currency = userBalance.currency)).apply {
                        this.sell.updateAmount(userBalance.amount.toString())
                    }
                }
            }
        }
    }

    private fun collectReceiveBalance() {
        viewModelScope.launch {
            balanceUseCase.receiveBalance.collect { userBalance ->
                _state.update { state ->
                    state.copy(receive = userBalance.toBalance())
                }
            }
        }
    }

    data class State(
        val balances: List<Balance> = emptyList(),
        val sell: SellBalance = SellBalance(),
        val receive: Balance = Balance(
            currency = DEFAULT_USER_RECEIVE_CURRENCY,
            amount = 0.0.roundToTwoDecimalPlaces()
        ),
        val currencies: List<String> = emptyList(),
        val dialogState: DialogState = DialogState(),
    ) {
        fun toStringBalances(): List<String> = balances.map { it.toString() }
    }

    data class SellBalance(val currency: String = "") {
        var amount by mutableStateOf("0.00")
            private set

        private val threeSignsAfterDot = Regex("^\\d+\\.\\d{3}")

        fun updateAmount(value: String) {
            amount = threeSignsAfterDot.replace(value, value.dropLast(1))
        }

        override fun toString() = "$amount $currency"
    }

    data class Balance(
        val currency: String = "",
        val amount: BigDecimal = BigDecimal("0.00")
    ) {
        override fun toString() = "$amount $currency"
    }

    data class DialogState(
        val sell: Balance = Balance(),
        val receive: Balance = Balance(),
        val fee: Balance = Balance()
    )

    sealed class SideEffects {
        data object Default : SideEffects()
        data object ShowDialog : SideEffects()
    }
}