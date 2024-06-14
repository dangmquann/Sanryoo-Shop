package com.sanryoo.shopping.feature.presentation.using.cart

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.sanryoo.shopping.R
import com.sanryoo.shopping.feature.domain.model.Order
import com.sanryoo.shopping.feature.domain.model.Product
import com.sanryoo.shopping.feature.presentation.using.cart.component.CartItem
import com.sanryoo.shopping.feature.presentation.using.cart.component.bottomsheet.EditOrderInCart
import com.sanryoo.shopping.feature.util.Screen
import com.sanryoo.shopping.feature.util.decimalFormat
import com.sanryoo.shopping.ui.theme.Primary
import kotlinx.coroutines.flow.collectLatest

@ExperimentalMaterialApi
@Composable
fun CartScreen(
    navController: NavHostController,
    viewModel: CartViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState(
        initialValue = Hidden,
        animationSpec = tween(500),
        confirmStateChange = { it != HalfExpanded }
    )
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is CartUiEvent.BackToPrevScreen -> {
                    navController.popBackStack()
                }

                is CartUiEvent.SetShowBottomSheet -> {
                    sheetState.animateTo(if (event.status) Expanded else Hidden)
                }

                is CartUiEvent.NavigateToProduct -> {
                    val productJson = Gson().toJson(event.product)
                    val encodedJson = Uri.encode(productJson)
                    navController.navigate(Screen.Product.route + "?product=$encodedJson")
                }

                is CartUiEvent.CheckOut -> {
                    val ordersJson = Gson().toJson(event.orders)
                    val encodeJson = Uri.encode(ordersJson)
                    navController.navigate(Screen.CheckOut.route + "?orders=$encodeJson")
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    CartContent(
        sheetState = sheetState,
        state = state,
        onCartChange = viewModel::onCartChange,
        deleteOrderInCart = viewModel::deleteOrderInCart,
        onEditOrderChange = viewModel::onEditOrderChange,
        editOrder = viewModel::editOrder,
        onBack = { viewModel.onUiEvent(CartUiEvent.BackToPrevScreen) },
        checkOut = { viewModel.onUiEvent(CartUiEvent.CheckOut(it)) },
        viewProduct = { viewModel.onUiEvent(CartUiEvent.NavigateToProduct(it)) },
        setShowBottomSheet = { viewModel.onUiEvent(CartUiEvent.SetShowBottomSheet(it)) },
    )
}

@ExperimentalMaterialApi
@Composable
private fun CartContent(
    sheetState: ModalBottomSheetState,
    state: CartState = CartState(),
    onCartChange: (List<CartOrder>) -> Unit = {},
    onEditOrderChange: (Order) -> Unit = {},
    editOrder: () -> Unit = {},
    setShowBottomSheet: (Boolean) -> Unit = {},
    onBack: () -> Unit = {},
    checkOut: (List<Order>) -> Unit = {},
    viewProduct: (Product) -> Unit = {},
    deleteOrderInCart: (Order) -> Unit = {}
) {
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetBackgroundColor = Color.White,
        sheetContent = {
            EditOrderInCart(
                order = state.editOrder,
                onEditOrderChange = onEditOrderChange,
                editOrder = editOrder,
                hideBottomSheet = { setShowBottomSheet(false) }
            )
        }
    ) {
        Scaffold(
            topBar = {
                Surface(
                    color = MaterialTheme.colors.background,
                    contentColor = MaterialTheme.colors.onBackground,
                    elevation = 8.dp,
                ) {
                    Box(
                        modifier = Modifier
                            .statusBarsPadding()
                            .fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Icon back",
                            tint = Primary,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(10.dp)
                                .clip(CircleShape)
                                .size(30.dp)
                                .clickable(
                                    interactionSource = MutableInteractionSource(),
                                    indication = null,
                                    onClick = onBack
                                )
                        )
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    SpanStyle(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                ) {
                                    append("Shopping Cart")
                                }
                                withStyle(
                                    SpanStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                ) {
                                    append(" (${state.orders.size})")
                                }
                            },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            },
            bottomBar = {
                if (state.orders.isNotEmpty()) {
                    Surface(
                        color = MaterialTheme.colors.background,
                        contentColor = MaterialTheme.colors.onBackground,
                        elevation = 8.dp,
                    ) {
                        Row(
                            modifier = Modifier
                                .navigationBarsPadding()
                                .fillMaxWidth()
                                .height(55.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.fillMaxHeight(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val checked = state.orders.all { it.checked }
                                Box(
                                    modifier = Modifier
                                        .padding(start = 15.dp, end = 10.dp)
                                        .size(30.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(if (checked) Primary else Color.White)
                                        .border(
                                            1.dp,
                                            if (checked) Primary else Color.Black,
                                            RoundedCornerShape(5.dp)
                                        )
                                        .clickable(
                                            interactionSource = MutableInteractionSource(),
                                            indication = null,
                                            onClick = {
                                                onCartChange(state.orders.map { it.copy(checked = !checked) })
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (checked) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.tick),
                                            contentDescription = "Icon Tick",
                                            modifier = Modifier.size(20.dp),
                                            tint = Color.White
                                        )
                                    }
                                }
                                Text(
                                    text = "All",
                                    modifier = Modifier.padding(end = 15.dp),
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .fillMaxHeight(0.7f)
                                    .background(Color.Gray)
                            )
                            Text(
                                text = "Total: ${decimalFormat.format(state.orders.getTotalCost())}đ",
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .weight(1f),
                                color = Color.Red
                            )
                            Box(
                                modifier = Modifier
                                    .width(130.dp)
                                    .fillMaxHeight()
                                    .background(Primary)
                                    .clickable(
                                        enabled = state.orders.any { it.checked },
                                        onClick = {
                                            val orders = state.orders
                                                .filter { it.checked }
                                                .map { it.order }
                                            checkOut(orders)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Check out (${state.orders.count { it.checked }})",
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        ) {
            LazyColumn {
                if (state.orders.isEmpty()) {
                    item {
                        Text(
                            text = "You have not added any products to your cart yet",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp, vertical = 10.dp),
                        )
                    }
                }
                itemsIndexed(state.orders) { index, cartOrder ->
                    CartItem(
                        modifier = Modifier.fillMaxWidth(),
                        cartOrder = cartOrder,
                        onToggleChecked = {
                            val tempList = state.orders.toMutableList()
                            tempList[index] =
                                tempList[index].copy(checked = !tempList[index].checked)
                            onCartChange(tempList)
                        },
                        onClickImage = { viewProduct(cartOrder.order.product) },
                        onEdit = {
                            onEditOrderChange(cartOrder.order)
                            setShowBottomSheet(true)
                        },
                        onDelete = { deleteOrderInCart(cartOrder.order) }
                    )
                    Divider(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
    BackHandler(
        enabled = sheetState.isVisible,
        onBack = { setShowBottomSheet(false) }
    )
}

fun List<CartOrder>.getTotalCost(): Long {
    val result = this.filter {
        it.checked
    }.sumOf {
        it.order.product.price * it.order.quantity
    }
    return result
}