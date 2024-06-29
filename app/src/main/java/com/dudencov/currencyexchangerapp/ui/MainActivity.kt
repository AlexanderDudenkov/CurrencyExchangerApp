package com.dudencov.currencyexchangerapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import com.dudencov.currencyexchangerapp.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel = hiltViewModel<MainViewModel>()

            val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
            lifecycleOwner.lifecycle.addObserver(mainViewModel)

            val state by mainViewModel.state.collectAsState()
            val sideEffect by mainViewModel.effects.collectAsState(MainViewModel.SideEffects.Default)

            var dialogVisible by remember { mutableStateOf(false) }

            dialogVisible = when (sideEffect) {
                is MainViewModel.SideEffects.ShowDialog -> true
                is MainViewModel.SideEffects.Default -> false
            }

            AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopBar() },
                ) { innerPadding ->
                    Content(
                        modifier = Modifier.padding(innerPadding),
                        state = state,
                        onSellCurrencySelected = { mainViewModel.onSellCurrencySelected(it) },
                        onReceiveCurrencySelected = { mainViewModel.onReceiveCurrencySelected(it) },
                        onSubmit = { mainViewModel.onSubmit() }
                    )

                    if (dialogVisible) {
                        Dialog(
                            toCurrency = state.dialogState.receive,
                            fromCurrency = state.dialogState.sell,
                            fee = state.dialogState.fee
                        ) { mainViewModel.onDone() }
                    }
                }
            }
        }
    }
}