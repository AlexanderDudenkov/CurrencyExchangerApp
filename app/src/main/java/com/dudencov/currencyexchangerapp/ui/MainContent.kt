package com.dudencov.currencyexchangerapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dudencov.currencyexchangerapp.R
import com.dudencov.currencyexchangerapp.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text("Currency converter")
        },
    )
}

@Composable
fun Content(
    modifier: Modifier = Modifier,
    state: MainViewModel.State,
    onSellCurrencySelected: (currency: String) -> Unit,
    onReceiveCurrencySelected: (currency: String) -> Unit,
    onSubmit: () -> Unit
) {
    Box(
        modifier = modifier.then(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
    ) {
        Column {
            Text(text = "MY BALANCES")

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalScrollableList(items = state.toStringBalances())

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "CURRENCY EXCHANGE")

            Spacer(modifier = Modifier.height(16.dp))

            ExchangeRow(
                label = "Sell",
                currencies = state.currencies,
                currentCurrency = state.sell.currency,
                onCurrencySelected = onSellCurrencySelected,
                amountFieldContent = {
                    TextField(
                        value = state.sell.amount,
                        onValueChange = { state.sell.updateAmount(it) },
                        modifier = Modifier.widthIn(min = 50.dp, max = 100.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.Transparent)
                    )
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            ExchangeRow(
                label = "Receive",
                currencies = state.currencies,
                currentCurrency = state.receive.currency,
                onCurrencySelected = onReceiveCurrencySelected,
                amountFieldContent = {
                    Text(
                        text = state.receive.amount.toString(),
                        modifier = Modifier.widthIn(min = 50.dp, max = 100.dp),
                    )
                }
            )
        }

        Button(
            onClick = onSubmit,
            modifier = Modifier
                .width(200.dp)
                .align(Alignment.BottomCenter)
        ) {
            Text(text = "Submit")
        }
    }
}

@Composable
private fun ExchangeRow(
    modifier: Modifier = Modifier,
    label: String,
    currencies: List<String>,
    currentCurrency: String,
    onCurrencySelected: (String) -> Unit,
    amountFieldContent: @Composable RowScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(currentCurrency) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label)
        Spacer(
            modifier = Modifier
                .width(16.dp)
                .weight(1f)
        )

        amountFieldContent()

        Spacer(modifier = Modifier.width(16.dp))
        Text(text = currentCurrency)

        Box {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_down_arrow),
                    contentDescription = null
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                currencies.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            selectedItem = item
                            onCurrencySelected(selectedItem)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HorizontalScrollableList(items: List<String>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
        items(items) { item ->
            Row {
                Text(text = item)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Preview() {
    AppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { TopBar() },
        ) { innerPadding ->
            Content(
                modifier = Modifier.padding(innerPadding),
                state = MainViewModel.State(),
                onSellCurrencySelected = {},
                onReceiveCurrencySelected = {},
                onSubmit = {}
            )
        }
    }
}