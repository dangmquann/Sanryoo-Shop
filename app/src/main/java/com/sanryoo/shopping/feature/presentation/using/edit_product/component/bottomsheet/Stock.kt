package com.sanryoo.shopping.feature.presentation.using.edit_product.component.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanryoo.shopping.R
import com.sanryoo.shopping.feature.domain.model.Stock
import com.sanryoo.shopping.feature.domain.model.Variation
import com.sanryoo.shopping.ui.theme.Primary

@Composable
fun Stock(
    variations: List<Variation> = emptyList(),
    stocks: List<Stock> = emptyList(),
    hideBottomSheet: () -> Unit = {},
    setStocks: (List<Stock>) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = { focusManager.clearFocus() }
            )
    ) {
        if (stocks.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "You have to complete variations before",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(20.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 10.dp)
                        .size(30.dp)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = {
                                focusManager.clearFocus()
                                hideBottomSheet()
                            }
                        ),
                    painter = painterResource(id = R.drawable.close),
                    contentDescription = "Icon close"
                )
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Stock",
                    fontSize = 26.sp,
                )
            }
            Divider(modifier = Modifier.fillMaxWidth())
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.background)
                    ) {
                        variations.forEach {
                            Text(
                                text = it.name,
                                modifier = Modifier
                                    .padding(15.dp)
                                    .weight(1f),
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp,
                                color = Primary
                            )
                        }
                        Text(
                            text = "Stock",
                            modifier = Modifier
                                .padding(15.dp)
                                .weight(1f),
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            color = Primary
                        )
                    }
                    Divider(modifier = Modifier.fillMaxWidth())
                }
                itemsIndexed(stocks) { index, stock ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.background),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        stock.variations.forEach { variation ->
                            Text(
                                text = variation,
                                modifier = Modifier
                                    .padding(15.dp)
                                    .weight(1f)
                            )
                        }
                        OutlinedTextField(
                            value = if (stock.quantity > 0) stock.quantity.toString() else "",
                            onValueChange = {
                                try {
                                    if (it.isBlank()) {
                                        val tempList = stocks.toMutableList()
                                        tempList[index] = tempList[index].copy(quantity = 0)
                                        setStocks(tempList)
                                    } else if (it.length <= 7 && it.toLong() > 0) {
                                        val tempList = stocks.toMutableList()
                                        tempList[index] =
                                            tempList[index].copy(quantity = it.toLong())
                                        setStocks(tempList)
                                    }
                                } catch (_: Exception) {
                                }
                            },
                            modifier = Modifier
                                .padding(15.dp)
                                .weight(1f),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            )
                        )
                    }
                    Divider(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}