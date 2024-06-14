package com.sanryoo.shopping.feature.presentation.using.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.sanryoo.shopping.R
import com.sanryoo.shopping.feature.domain.model.Order
import com.sanryoo.shopping.feature.domain.model.getTotalCost
import com.sanryoo.shopping.feature.presentation.using.checkout.component.CheckOutItem
import com.sanryoo.shopping.feature.util.decimalFormat
import com.sanryoo.shopping.ui.theme.Primary
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CheckOutScreen(
    orders: List<Order>,
    navController: NavHostController,
    scaffoldState: ScaffoldState,
    viewModel: CheckOutViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    LaunchedEffect(Unit) {
        viewModel.onOrdersChange(orders)
    }
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is CheckOutUiEvent.ClearFocus -> {
                    focusManager.clearFocus()
                }

                is CheckOutUiEvent.BackToPrevScreen -> {
                    navController.popBackStack()
                }

                is CheckOutUiEvent.ShowSnackBar -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    CheckOutContent(
        state = state,
        onOrdersChange = viewModel::onOrdersChange,
        placeOrder = viewModel::placeOrder,
        onClearFocus = { viewModel.onUiEvent(CheckOutUiEvent.ClearFocus) },
        onBack = { viewModel.onUiEvent(CheckOutUiEvent.BackToPrevScreen) }
    )
}

@Composable
private fun CheckOutContent(
    state: CheckOutState = CheckOutState(),
    onOrdersChange: (List<Order>) -> Unit = {},
    placeOrder: () -> Unit = {},
    onClearFocus: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    if (state.loading) {
        AlertDialog(
            onDismissRequest = {},
            backgroundColor = Color.Transparent,
            buttons = {
                CircularProgressIndicator(
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(50.dp)
                )
            }
        )
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = onClearFocus
            ),
        topBar = {
            Surface(
                color = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.onBackground,
                elevation = 16.dp,
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
                        text = "Check Out (${state.orders.size})",
                        fontSize = 20.sp,
                        color = Primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        },
        bottomBar = {
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
                    val total = state.orders.sumOf { it.getTotalCost() }
                    Column(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .weight(1f),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Total:")
                        Text(
                            text = "${decimalFormat.format(total)}đ",
                            color = Color.Red,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .fillMaxHeight()
                            .background(Primary)
                            .clickable (onClick = placeOrder),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Place Order",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colors.surface)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.location),
                            contentDescription = "Icon Location",
                            modifier = Modifier
                                .padding(5.dp)
                                .size(25.dp)
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(5.dp)
                        ) {
                            Text(text = "Receiver's Information: ")
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(text = "Name: ${state.user.name}")
                            Text(text = "Phone Number: ${state.user.phoneNumber}")
                            Text(text = "Address: ${state.user.address}")
                        }
                    }
                }
                itemsIndexed(state.orders) { index, order ->
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                    )
                    CheckOutItem(
                        modifier = Modifier.fillMaxWidth(),
                        order = order,
                        onOrderChange = { newOrder ->
                            val tempList = state.orders.toMutableList()
                            tempList[index] = newOrder
                            onOrdersChange(tempList)
                        }
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(55.dp)
            )
        }
    }
}