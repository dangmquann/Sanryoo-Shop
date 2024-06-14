package com.sanryoo.shopping.feature.presentation.using.shop

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.sanryoo.shopping.R
import com.sanryoo.shopping.feature.domain.model.Product
import com.sanryoo.shopping.feature.presentation._component.ItemProduct
import com.sanryoo.shopping.feature.presentation._component.shimmerEffect
import com.sanryoo.shopping.feature.presentation.using.product.component.ProductTopBar
import com.sanryoo.shopping.feature.util.Screen
import com.sanryoo.shopping.ui.theme.Primary
import kotlinx.coroutines.flow.collectLatest

@ExperimentalMaterialApi
@Composable
fun ShopScreen(
    uid: String,
    navController: NavHostController,
    viewModel: ShopViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.getShop(uid)
    }
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is ShopUiEvent.BackToPrevScreen -> {
                    navController.popBackStack()
                }

                is ShopUiEvent.NavigateToLogIn -> {
                    navController.navigate(Screen.LogIn.route)
                }

                is ShopUiEvent.NavigateToCart -> {
                    navController.navigate(Screen.Cart.route)
                }

                is ShopUiEvent.NavigateToChats -> {
                    navController.navigate(Screen.Chats.route)
                }

                is ShopUiEvent.NavigateToMessage -> {
                    navController.navigate(Screen.Message.route + "?othersId=${event.otherId}")
                }

                is ShopUiEvent.ViewProduct -> {
                    val productJson = Gson().toJson(event.product)
                    val encodedJson = Uri.encode(productJson)
                    navController.navigate(Screen.Product.route + "?product=$encodedJson")
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    ShopContent(
        state = state,
        likeShop = viewModel::likeShop,
        unlikeShop = viewModel::unlikeShop,
        onClickCart = viewModel::onClickCart,
        onClickChats = viewModel::onClickChats,
        onClickChatWithShop = {viewModel.onUiEvent(ShopUiEvent.NavigateToMessage(it))},
        onBack = { viewModel.onUiEvent(ShopUiEvent.BackToPrevScreen) },
        viewProduct = { viewModel.onUiEvent(ShopUiEvent.ViewProduct(it)) }
    )
}

@ExperimentalMaterialApi
@Composable
private fun ShopContent(
    state: ShopState = ShopState(),
    likeShop: () -> Unit = {},
    onClickCart: () -> Unit = {},
    onClickChats: () -> Unit = {},
    onClickChatWithShop: (String) -> Unit = {},
    unlikeShop: () -> Unit = {},
    viewProduct: (Product) -> Unit = {},
    onBack: () -> Unit = {}
) {
    var firstItemOffset by remember {
        mutableStateOf(Offset.Zero)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            content = {
                item(span = { GridItemSpan(2) }) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .onGloballyPositioned { coordinates ->
                                    val offset = coordinates.positionInWindow()
                                    firstItemOffset = offset
                                }
                        ) {
                            AsyncImage(
                                model = state.shop.coverPhoto,
                                contentDescription = "Cover Photo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 40.dp)
                                    .background(MaterialTheme.colors.surface),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .padding(start = 20.dp)
                                    .size(120.dp)
                                    .align(Alignment.BottomStart)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colors.background),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = state.shop.profilePicture.ifBlank { R.drawable.user },
                                    contentDescription = "Profile picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(110.dp)
                                        .clip(CircleShape)
                                        .shimmerEffect()
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = state.shop.name,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .weight(1f)
                            )
                            if (state.user.uid != state.shop.uid) {
                                Box(
                                    modifier = Modifier
                                        .padding(end = 5.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(Primary)
                                        .clickable { onClickChatWithShop(state.shop.uid) }
                                ) {
                                    Text(
                                        text = "Chat",
                                        color = Color.White,
                                        modifier = Modifier.padding(
                                            horizontal = 20.dp,
                                            vertical = 5.dp
                                        )
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .padding(end = 5.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(if (state.liked) Primary else Color.White)
                                        .border(1.dp, Primary, RoundedCornerShape(5.dp))
                                        .clickable {
                                            if (state.liked)
                                                unlikeShop()
                                            else
                                                likeShop()
                                        }
                                ) {
                                    Text(
                                        text = if (state.liked) "Liked" else " Like ",
                                        color = if (state.liked) Color.White else Primary,
                                        modifier = Modifier.padding(
                                            horizontal = 20.dp,
                                            vertical = 5.dp
                                        )
                                    )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${state.numberOfLikes}",
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                            Icon(
                                painter = painterResource(id = R.drawable.like),
                                contentDescription = "Icon Like",
                                tint = Color.Red,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        Text(
                            text = "Bio: ${state.shop.bio}",
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                        Text(
                            text = "Address: ${state.shop.address}",
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
                items(state.products) { product ->
                    ItemProduct(
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth()
                            .aspectRatio(3 / 5f),
                        product = product,
                        onClick = {
                            viewProduct(product)
                        }
                    )
                }
                item(span = { GridItemSpan(2) }) {
                    Box(modifier = Modifier
                        .navigationBarsPadding()
                        .fillMaxWidth()
                    )
                }
            }
        )
        ProductTopBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(),
            firstItemOffset = firstItemOffset,
            numberOfCart = state.numberOfCart,
            numberOfChats = state.numberOfChats,
            onBack = onBack,
            onClickCart = onClickCart,
            onClickChats = onClickChats
        )
    }
}