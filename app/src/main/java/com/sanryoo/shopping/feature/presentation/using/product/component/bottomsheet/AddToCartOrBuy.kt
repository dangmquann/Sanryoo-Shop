package com.sanryoo.shopping.feature.presentation.using.product.component.bottomsheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sanryoo.shopping.R
import com.sanryoo.shopping.feature.domain.model.Order
import com.sanryoo.shopping.feature.util.decimalFormat

@ExperimentalFoundationApi
@Composable
fun AddToCartOrBuy(
    order: Order,
    label: String,
    hideBottomSheet: () -> Unit,
    onClickButton: () -> Unit = {},
    onAddToCartChange: (Order) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val showStock = order.product.stocks.filter {
        it.variations.containsAll(
            order.variations.values
        )
    }.sumOf {
        it.quantity
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = { focusManager.clearFocus() }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
        ) {
            AsyncImage(
                model = if (order.product.images.isNotEmpty()) order.product.images[0] else "",
                contentDescription = "Image",
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxHeight()
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        painter = painterResource(id = R.drawable.close),
                        contentDescription = "Icon close",
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(10.dp)
                            .size(25.dp)
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick = {
                                    focusManager.clearFocus()
                                    hideBottomSheet()
                                }
                            )
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Price: ${decimalFormat.format(order.product.price)}đ",
                    fontSize = 18.sp,
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                )
                Text(
                    text = "Stock: $showStock",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Divider(modifier = Modifier.fillMaxWidth())
            }
            items(order.product.variations) { variation ->
                Text(text = variation.name, modifier = Modifier.padding(5.dp))
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    itemsIndexed(variation.child) { childIndex, childVariation ->
                        val value = order.variations[variation.name] ?: ""
                        val chooseVariation = variation.child.indexOf(value)
                        val color = if (chooseVariation == childIndex)
                            Color.Red
                        else
                            MaterialTheme.colors.onBackground
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .border(
                                    width = 1.dp,
                                    color = color,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .clickable {
                                    val variations = order.variations.toMutableMap()
                                    variations[variation.name] = childVariation
                                    onAddToCartChange(order.copy(variations = variations))
                                }
                        ) {
                            Text(
                                text = childVariation,
                                modifier = Modifier.padding(horizontal = 25.dp, vertical = 5.dp),
                                color = color,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
                Divider(modifier = Modifier.fillMaxWidth())
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = "Quantity", modifier = Modifier.padding(start = 10.dp))
                    Row(modifier = Modifier.padding(end = 10.dp)) {
                        Box(
                            modifier = Modifier
                                .border(1.dp, Color.Gray)
                                .clickable(
                                    interactionSource = MutableInteractionSource(),
                                    indication = null,
                                    onClick = {
                                        focusManager.clearFocus()
                                        if (order.quantity - 1 >= 1) {
                                            onAddToCartChange(order.copy(quantity = order.quantity - 1))
                                        }
                                    },
                                    enabled = order.variations.size == order.product.variations.size
                                )
                        ) {
                            Text(
                                text = "-",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                        Box(modifier = Modifier.border(1.dp, Color.Gray)) {
                            BasicTextField(
                                value = if (order.quantity > 0) order.quantity.toString() else "",
                                onValueChange = {
                                    if (it.isEmpty()) {
                                        onAddToCartChange(order.copy(quantity = 0))
                                    }
                                    try {
                                        val newQuantity = it.toLong()
                                        if (newQuantity in 1..showStock) {
                                            onAddToCartChange(order.copy(quantity = newQuantity))
                                        }
                                    } catch (_: Exception) {
                                    }
                                },
                                modifier = Modifier.padding(vertical = 5.dp),
                                textStyle = TextStyle(
                                    color = Color.Red,
                                    textAlign = TextAlign.Center,
                                    fontSize = 16.sp
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                cursorBrush = SolidColor(Color.Red),
                                enabled = order.variations.size == order.product.variations.size
                            )
                        }
                        Box(
                            modifier = Modifier
                                .border(1.dp, Color.Gray)
                                .clickable(
                                    interactionSource = MutableInteractionSource(),
                                    indication = null,
                                    onClick = {
                                        focusManager.clearFocus()
                                        if (order.quantity + 1 <= showStock) {
                                            onAddToCartChange(order.copy(quantity = order.quantity + 1))
                                        }
                                    },
                                    enabled = order.variations.size == order.product.variations.size
                                )
                        ) {
                            Text(
                                text = "+",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
            item {
                Divider(modifier = Modifier.fillMaxWidth())
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Price:",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(5.dp)
                    )
                    Text(
                        text = "${decimalFormat.format(order.product.price * order.quantity)}đ",
                        fontSize = 18.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onClickButton,
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .padding(10.dp),
                enabled = order.quantity in 1..showStock && order.variations.size == order.product.variations.size
            ) {
                Text(text = label)
            }
        }
    }
}