package com.sanryoo.shopping.feature.presentation.using.my_purchase

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.sanryoo.shopping.R
import com.sanryoo.shopping.feature.domain.model.Order
import com.sanryoo.shopping.feature.domain.model.Product
import com.sanryoo.shopping.feature.presentation.using.my_purchase.component.MyPurchaseTag
import com.sanryoo.shopping.feature.util.OrderStatus.CANCELLED
import com.sanryoo.shopping.feature.util.OrderStatus.ORDERED
import com.sanryoo.shopping.feature.util.OrderStatus.SHIPPED
import com.sanryoo.shopping.feature.util.OrderStatus.SHIPPING
import com.sanryoo.shopping.feature.util.Screen
import com.sanryoo.shopping.ui.theme.Primary
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MyPurchaseScreen(
    navController: NavHostController,
    viewModel: MyPurchaseViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is MyPurchaseUiEvent.BackToPrevScreen -> {
                    navController.popBackStack()
                }

                is MyPurchaseUiEvent.ViewProduct -> {
                    val productJson = Gson().toJson(event.product)
                    val encodeJson = Uri.encode(productJson)
                    navController.navigate(Screen.Product.route + "?product=$encodeJson")
                }

                is MyPurchaseUiEvent.CheckOut -> {
                    val ordersJson = Gson().toJson(event.orders)
                    val encodeJson = Uri.encode(ordersJson)
                    navController.navigate(Screen.CheckOut.route + "?orders=$encodeJson")
                }

                is MyPurchaseUiEvent.Review -> {
                    val orderJson = Gson().toJson(event.order)
                    val encodeJson = Uri.encode(orderJson)
                    navController.navigate(Screen.Review.route + "?order=$encodeJson")
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    MyPurchaseContent(
        state = state,
        setTab = viewModel::setNewTab,
        onCancelOrder = viewModel::cancelOrder,
        onBack = { viewModel.onUiEvent(MyPurchaseUiEvent.BackToPrevScreen) },
        onViewProduct = { viewModel.onUiEvent(MyPurchaseUiEvent.ViewProduct(it)) },
        onBuyAgain = { viewModel.onUiEvent(MyPurchaseUiEvent.CheckOut(listOf(it.copy(oid = "")))) },
        onReview = { viewModel.onUiEvent(MyPurchaseUiEvent.Review(it)) }
    )
}

@Composable
private fun MyPurchaseContent(
    state: MyPurchaseState = MyPurchaseState(),
    setTab: (Int) -> Unit = {},
    onViewProduct: (Product) -> Unit = {},
    onCancelOrder: (Order) -> Unit = {},
    onBuyAgain: (Order) -> Unit = {},
    onReview: (Order) -> Unit = {},
    onBack: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.onBackground,
                elevation = 16.dp,
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
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
                            text = "My Purchase",
                            fontSize = 20.sp,
                            color = Primary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    val tabs = listOf("All", ORDERED, SHIPPING, SHIPPED, CANCELLED)
                    ScrollableTabRow(
                        selectedTabIndex = state.currentTab,
                        backgroundColor = Color.White,
                        contentColor = Primary,
                        edgePadding = 0.dp
                    ) {
                        tabs.forEachIndexed { index, tabName ->
                            val numberOfOrders = when (index) {
                                0 -> state.allOrders.size
                                1 -> state.orderedOrders.size
                                2 -> state.shippingOrders.size
                                3 -> state.shippedOrders.size
                                4 -> state.cancelledOrders.size
                                else -> 0
                            }
                            Tab(
                                selected = index == state.currentTab,
                                onClick = { setTab(index) },
                                text = {
                                    Text(
                                        text = "$tabName${if (numberOfOrders > 0) " ($numberOfOrders)" else ""}",
                                        modifier = Modifier.padding(
                                            horizontal = 10.dp,
                                            vertical = 5.dp
                                        )
                                    )
                                },
                                selectedContentColor = Primary,
                                unselectedContentColor = Color.Black
                            )
                        }
                    }
                }
            }
        }
    ) {
        AnimatedContent(
            modifier = Modifier.fillMaxSize(),
            targetState = state.currentTab,
            transitionSpec = {
                slideInHorizontally(
                    initialOffsetX = {
                        if (state.oldTab > state.currentTab) -it else it
                    }
                ) with slideOutHorizontally(
                    targetOffsetX = {
                        if (state.oldTab > state.currentTab) it else -it
                    }
                )
            },
            content = { tabIndex ->
                when (tabIndex) {
                    //All
                    0 -> MyPurchaseTag(
                        orders = state.allOrders,
                        onViewProduct = onViewProduct,
                        onCancel = onCancelOrder,
                        onBuyAgain = onBuyAgain,
                        onReview = onReview
                    )

                    //Ordered, Shipping
                    1 -> MyPurchaseTag(
                        orders = state.orderedOrders,
                        onViewProduct = onViewProduct,
                        onCancel = onCancelOrder
                    )
                    2 -> MyPurchaseTag(
                        orders = state.shippingOrders,
                        onViewProduct = onViewProduct,
                        onCancel = onCancelOrder
                    )

                    //Shipped
                    3 -> MyPurchaseTag(
                        orders = state.shippedOrders,
                        onViewProduct = onViewProduct,
                        onBuyAgain = onBuyAgain,
                        onReview = onReview
                    )

                    //Cancelled
                    4 -> MyPurchaseTag(
                        orders = state.allOrders,
                        onViewProduct = onViewProduct,
                        onCancel = onCancelOrder,
                        onBuyAgain = onBuyAgain,
                    )
                }
            }
        )
    }
}