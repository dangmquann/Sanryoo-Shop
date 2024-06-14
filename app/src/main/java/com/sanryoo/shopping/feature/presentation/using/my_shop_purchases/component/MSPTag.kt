package com.sanryoo.shopping.feature.presentation.using.my_shop_purchases.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sanryoo.shopping.feature.domain.model.Order

@Composable
fun MyShopPurchaseTag(
    orders: List<Order> = emptyList(),
    onConfirmOrder: (Order) -> Unit = {},
    onCancelOrder: (Order) -> Unit = {},
    onConfirmShipped: (Order) -> Unit = {}
) {
    LazyColumn {
        if (orders.isEmpty()) {
            item {
                Text(
                    text = "No Order yet",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 20.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        itemsIndexed(orders) { index, order ->
            MyShopPurchaseItem(
                modifier = Modifier.fillMaxWidth(),
                order = order,
                onConfirmOrder = onConfirmOrder,
                onCancelOrder = onCancelOrder,
                onConfirmShipped = onConfirmShipped
            )
            if (index != orders.size - 1) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .background(MaterialTheme.colors.surface)
                )
            }
        }
        item {
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}