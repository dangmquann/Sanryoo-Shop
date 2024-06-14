package com.sanryoo.shopping.feature.presentation.using.cart.component

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sanryoo.shopping.R
import com.sanryoo.shopping.feature.domain.model.getTotalCost
import com.sanryoo.shopping.feature.presentation.using.cart.CartOrder
import com.sanryoo.shopping.feature.util.decimalFormat
import com.sanryoo.shopping.ui.theme.Primary
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.math.roundToInt

@ExperimentalMaterialApi
@Composable
fun CartItem(
    modifier: Modifier = Modifier,
    cartOrder: CartOrder = CartOrder(),
    onToggleChecked: () -> Unit = {},
    onClickImage: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val pxValue = with(LocalDensity.current) { 150.dp.toPx() }
    val swipeState = rememberSwipeableState(initialValue = 0)
    Box(
        modifier = modifier.height(120.dp)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(160.dp)
                .background(Color(0xFFF39407)),
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(75.dp)
                    .clickable {
                        scope.launch {
                            swipeState.animateTo(0)
                            onEdit()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Edit", color = Color.White)
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(75.dp)
                    .background(Color.Red)
                    .clickable {
                        scope.launch {
                            swipeState.animateTo(0)
                            onDelete()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Delete", color = Color.White)
            }
        }
        Row(
            modifier = Modifier
                .offset {
                    IntOffset(swipeState.offset.value.roundToInt(), 0)
                }
                .fillMaxSize()
                .swipeable(
                    state = swipeState,
                    anchors = mapOf(
                        0f to 0,
                        -pxValue to 1
                    ),
                    orientation = Orientation.Horizontal,
                    thresholds = { _, _ ->
                        FractionalThreshold(0.3f)
                    }
                )
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .size(30.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(if (cartOrder.checked) Primary else Color.White)
                        .border(
                            1.dp,
                            if (cartOrder.checked) Primary else Color.Black,
                            RoundedCornerShape(5.dp)
                        )
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = onToggleChecked
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (cartOrder.checked) {
                        Icon(
                            painter = painterResource(id = R.drawable.tick),
                            contentDescription = "Icon Tick",
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                    }
                }
            }
            AsyncImage(
                model = cartOrder.order.product.images[0],
                contentDescription = "image",
                modifier = Modifier
                    .padding(10.dp)
                    .size(100.dp)
                    .clickable(onClick = onClickImage),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(10.dp)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = onEdit
                    ),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = cartOrder.order.product.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "Variations: ${cartOrder.order.variations.customToString()}")
                Text(text = "Quantity: ${cartOrder.order.quantity}")
                Text(
                    text = "Total Cost: ${decimalFormat.format(cartOrder.order.getTotalCost())}đ",
                    color = Color.Red
                )
            }
        }
    }
}

fun Map<String, String>.customToString(): String {
    var result = ""
    val list = values.toList()
    list.forEachIndexed { index, value ->
        result += value
        if (index != list.size - 1) {
            result += ", "
        }
    }
    return result
}