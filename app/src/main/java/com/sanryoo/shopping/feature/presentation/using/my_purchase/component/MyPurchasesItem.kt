package com.sanryoo.shopping.feature.presentation.using.my_purchase.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sanryoo.shopping.feature.domain.model.Order
import com.sanryoo.shopping.feature.domain.model.Product
import com.sanryoo.shopping.feature.domain.model.getTotalCost
import com.sanryoo.shopping.feature.presentation.using.cart.component.customToString
import com.sanryoo.shopping.feature.util.OrderStatus.CANCELLED
import com.sanryoo.shopping.feature.util.OrderStatus.ORDERED
import com.sanryoo.shopping.feature.util.OrderStatus.SHIPPED
import com.sanryoo.shopping.feature.util.OrderStatus.SHIPPING
import com.sanryoo.shopping.feature.util.decimalFormat
import com.sanryoo.shopping.ui.theme.Primary
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
@Composable
fun MyPurchaseItem(
    modifier: Modifier = Modifier,
    order: Order = Order(),
    onViewProduct: (Product) -> Unit = {},
    onCancel: (Order) -> Unit = {},
    onBuyAgain: (Order) -> Unit = {},
    onReview: (Order) -> Unit = {}
) {
    val dateFormat = SimpleDateFormat("HH:mm dd/MM/yyyy")
    Column(modifier = modifier.background(Color.White)) {
        Row {
            AsyncImage(
                model = order.product.images[0],
                contentDescription = "Product's First Image",
                modifier = Modifier
                    .padding(10.dp)
                    .size(100.dp)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = { onViewProduct(order.product) }
                    ),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = order.product.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    modifier = Modifier.clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = { onViewProduct(order.product) }
                    )
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "Variations: ${order.variations.customToString()}")
                Text(text = "Price: ${decimalFormat.format(order.product.price)}đ")
                Text(text = "Quantity: ${order.quantity}")
            }
        }
        Divider(modifier = Modifier.fillMaxWidth())
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Message: ",
                modifier = Modifier.padding(10.dp)
            )
            Text(
                text = order.message.ifBlank { "No message" },
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp)
                    .padding(start = 10.dp),
                textAlign = TextAlign.End
            )
        }
        Divider(modifier = Modifier.fillMaxWidth())
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Ordered Date: ",
                modifier = Modifier.padding(10.dp)
            )
            Text(
                text = order.orderedDate?.let { dateFormat.format(it) } ?: "-",
                modifier = Modifier.padding(10.dp)
            )
        }
        Divider(modifier = Modifier.fillMaxWidth())
        if (order.status == SHIPPED) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Shipped Date: ",
                    modifier = Modifier.padding(10.dp)
                )
                Text(
                    text = order.shippedDate?.let { dateFormat.format(it) } ?: "-",
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
        if (order.status == CANCELLED) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Cancelled Date: ",
                    modifier = Modifier.padding(10.dp)
                )
                Text(
                    text = order.cancelledDate?.let { dateFormat.format(it) } ?: "-",
                    modifier = Modifier.padding(10.dp),
                )
            }
        }
        Divider(modifier = Modifier.fillMaxWidth())
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Status: ",
                modifier = Modifier.padding(10.dp)
            )
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .clip(RoundedCornerShape(100))
                    .background(
                        when (order.status) {
                            ORDERED -> Primary
                            SHIPPING -> Color.Blue
                            SHIPPED -> Color.Green
                            CANCELLED -> Color.Red
                            else -> Color.Black
                        }
                    )
            ) {
                Text(
                    text = order.status,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    color = Color.White
                )
            }
        }
        Divider(modifier = Modifier.fillMaxWidth())
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Order Total (${order.quantity} item): ",
                modifier = Modifier.padding(10.dp)
            )
            Text(
                text = "${decimalFormat.format(order.getTotalCost())}đ",
                fontSize = 18.sp,
                color = Color.Red,
                modifier = Modifier.padding(10.dp)
            )
        }
        Divider(modifier = Modifier.fillMaxWidth())
        if(order.status == SHIPPED && !order.reviewed) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable{ onReview(order) }
            ) {
                Text(
                    text = "Review",
                    textAlign = TextAlign.Center,
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
            }
            Divider(modifier = Modifier.fillMaxWidth())
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    when (order.status) {
                        ORDERED, SHIPPING -> onCancel(order)
                        SHIPPED, CANCELLED -> onBuyAgain(order)
                    }
                }
        ) {
            Text(
                text = when (order.status) {
                    ORDERED, SHIPPING -> "Cancel"
                    SHIPPED, CANCELLED -> "Buy Again"
                    else -> return@Box
                },
                textAlign = TextAlign.Center,
                color = Color.Red,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        Divider(modifier = Modifier.fillMaxWidth())
    }
}