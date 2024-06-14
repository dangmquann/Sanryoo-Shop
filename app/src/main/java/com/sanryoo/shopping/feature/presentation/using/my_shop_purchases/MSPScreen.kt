package com.sanryoo.shopping.feature.presentation.using.my_shop_purchases

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
import com.sanryoo.shopping.R
import com.sanryoo.shopping.feature.domain.model.Order
import com.sanryoo.shopping.feature.presentation.using.my_shop_purchases.component.MyShopPurchaseTag
import com.sanryoo.shopping.feature.util.OrderStatus.CANCELLED
import com.sanryoo.shopping.feature.util.OrderStatus.ORDERED
import com.sanryoo.shopping.feature.util.OrderStatus.SHIPPED
import com.sanryoo.shopping.feature.util.OrderStatus.SHIPPING
import com.sanryoo.shopping.ui.theme.Primary
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MyShopPurchaseScreen(
    navController: NavHostController,
    viewModel: MSPViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is MSPUiEvent.BackToMyShopScreen -> {
                    navController.popBackStack()
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    MyPurchaseContent(
        state = state,
        setTab = viewModel::setNewTab,
        onConfirmOrder = viewModel::confirmOrder,
        onCancelOrder = viewModel::cancelOrder,
        onConfirmShipped = viewModel::confirmShipped,
        onBack = { viewModel.onUiEvent(MSPUiEvent.BackToMyShopScreen) },
    )
}

@Composable
private fun MyPurchaseContent(
    state: MSPState = MSPState(),
    setTab: (Int) -> Unit = {},
    onConfirmOrder: (Order) -> Unit = {},
    onCancelOrder: (Order) -> Unit = {},
    onConfirmShipped: (Order) -> Unit = {},
    onBack: () -> Unit = {}
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
                            text = "My Shop's Purchase",
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
                    0 -> MyShopPurchaseTag(
                        orders = state.allOrders,
                        onConfirmOrder = onConfirmOrder,
                        onCancelOrder = onCancelOrder,
                        onConfirmShipped = onConfirmShipped
                    )

                    //Ordered
                    1 -> MyShopPurchaseTag(
                        orders = state.orderedOrders,
                        onConfirmOrder = onConfirmOrder,
                        onCancelOrder = onCancelOrder,
                    )

                    //Shipping
                    2 -> MyShopPurchaseTag(
                        orders = state.shippingOrders,
                        onCancelOrder = onCancelOrder,
                        onConfirmShipped = onConfirmShipped
                    )

                    //Shipped, Cancelled
                    3 -> MyShopPurchaseTag(state.shippedOrders)
                    4 -> MyShopPurchaseTag(state.cancelledOrders)
                }
            }
        )
    }
}