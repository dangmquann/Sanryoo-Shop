package com.sanryoo.shopping.feature.presentation.using.product

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue.Expanded
import androidx.compose.material.ModalBottomSheetValue.HalfExpanded
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.gson.Gson
import com.sanryoo.shopping.R
import com.sanryoo.shopping.feature.domain.model.Order
import com.sanryoo.shopping.feature.domain.model.Product
import com.sanryoo.shopping.feature.domain.model.getAvgRate
import com.sanryoo.shopping.feature.domain.model.getSold
import com.sanryoo.shopping.feature.presentation._component.ItemProduct
import com.sanryoo.shopping.feature.presentation._component.shimmerEffect
import com.sanryoo.shopping.feature.presentation.using.product.SheetContent.ADD_TO_CART
import com.sanryoo.shopping.feature.presentation.using.product.SheetContent.BUY_NOW
import com.sanryoo.shopping.feature.presentation.using.product.SheetContent.DEFAULT
import com.sanryoo.shopping.feature.presentation.using.product.component.ProductBottomBar
import com.sanryoo.shopping.feature.presentation.using.product.component.ProductTopBar
import com.sanryoo.shopping.feature.presentation.using.product.component.RatingBar
import com.sanryoo.shopping.feature.presentation.using.product.component.bottomsheet.AddToCartOrBuy
import com.sanryoo.shopping.feature.util.Screen
import com.sanryoo.shopping.feature.util.decimalFormat
import com.sanryoo.shopping.feature.util.toCategoryString
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun ProductScreen(
    product: Product,
    scaffoldState: ScaffoldState,
    navController: NavHostController,
    viewModel: ProductViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val sheetState = rememberModalBottomSheetState(
        initialValue = Hidden,
        animationSpec = tween(500),
        confirmStateChange = { it != HalfExpanded }
    )
    LaunchedEffect(Unit) {
        viewModel.getInformationProduct(product)
    }
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is ProductUiEvent.ClearFocus -> {
                    focusManager.clearFocus()
                }

                is ProductUiEvent.BackToPrevScreen -> {
                    navController.popBackStack()
                }

                is ProductUiEvent.NavigateToLogIn -> {
                    navController.navigate(Screen.LogIn.route)
                }

                is ProductUiEvent.NavigateToCart -> {
                    navController.navigate(Screen.Cart.route)
                }

                is ProductUiEvent.NavigateToChats -> {
                    navController.navigate(Screen.Chats.route)
                }

                is ProductUiEvent.NavigateToMessage -> {
                    navController.navigate(Screen.Message.route + "?othersId=${event.otherId}")
                }

                is ProductUiEvent.CheckOut -> {
                    val ordersJson = Gson().toJson(event.orders)
                    val encodeJson = Uri.encode(ordersJson)
                    navController.navigate(Screen.CheckOut.route + "?orders=$encodeJson")
                }

                is ProductUiEvent.ViewShop -> {
                    navController.navigate(Screen.Shop.route + "?uid=${event.uid}")
                }

                is ProductUiEvent.ViewProduct -> {
                    val productJson = Gson().toJson(event.product)
                    val encodedJson = Uri.encode(productJson)
                    navController.navigate(Screen.Product.route + "?product=$encodedJson")
                }

                is ProductUiEvent.ShowSnackBar -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.message)
                }

                is ProductUiEvent.SetShowBottomSheet -> {
                    sheetState.animateTo(if (event.status) Expanded else Hidden)
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    ProductContent(
        sheetState = sheetState,
        state = state,
        setFirstItemOffset = viewModel::setFirstItemOffset,
        setSheetContent = viewModel::setSheetContent,
        onClickAddToCart = viewModel::onClickAddToCart,
        onClickBuyNow = viewModel::onClickBuyNow,
        onClickCart = viewModel::onClickCart,
        onClickChatIcon = viewModel::onClickChats,
        onAddToCartChange = viewModel::onAddToCartChange,
        addToCart = viewModel::onAddToCart,
        onClickChatWithShop = {viewModel.onUiEvent(ProductUiEvent.NavigateToMessage(it))},
        checkOut = { viewModel.onUiEvent(ProductUiEvent.CheckOut(it)) },
        setShowBottomSheet = { viewModel.onUiEvent(ProductUiEvent.SetShowBottomSheet(it)) },
        viewShop = { viewModel.onUiEvent(ProductUiEvent.ViewShop(state.product.user.uid)) },
        viewProduct = { viewModel.onUiEvent(ProductUiEvent.ViewProduct(it)) },
        onBack = { viewModel.onUiEvent(ProductUiEvent.BackToPrevScreen) },
    )
}

@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
private fun ProductContent(
    sheetState: ModalBottomSheetState,
    state: ProductState = ProductState(),
    setFirstItemOffset: (Offset) -> Unit = {},
    setSheetContent: (SheetContent) -> Unit = {},
    onClickCart: () -> Unit = {},
    onClickChatIcon: () -> Unit = {},
    onClickChatWithShop: (String) -> Unit = {},
    onClickAddToCart: () -> Unit = {},
    onClickBuyNow: () -> Unit = {},
    onAddToCartChange: (Order) -> Unit = {},
    addToCart: () -> Unit = {},
    checkOut: (List<Order>) -> Unit = {},
    setShowBottomSheet: (Boolean) -> Unit = {},
    viewShop: () -> Unit = {},
    viewProduct: (Product) -> Unit = {},
    onBack: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetBackgroundColor = MaterialTheme.colors.background,
        sheetElevation = 0.dp,
        sheetContent = {
            when (state.sheetContent) {
                DEFAULT -> {
                    Box(modifier = Modifier.fillMaxSize())
                }

                ADD_TO_CART -> {
                    AddToCartOrBuy(
                        order = state.addToCart,
                        label = "Add to Cart",
                        hideBottomSheet = { setShowBottomSheet(false) },
                        onAddToCartChange = onAddToCartChange,
                        onClickButton = addToCart
                    )
                }

                BUY_NOW -> {
                    AddToCartOrBuy(
                        order = state.addToCart,
                        label = "Buy Now",
                        hideBottomSheet = { setShowBottomSheet(false) },
                        onAddToCartChange = onAddToCartChange,
                        onClickButton = {
                            scope.launch {
                                setShowBottomSheet(false)
                                delay(500)
                                checkOut(listOf(state.addToCart))
                            }
                        }
                    )
                }
            }
        },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (state.user.uid != state.product.user.uid) Modifier.navigationBarsPadding() else Modifier)
                    .padding(bottom = if (state.user.uid != state.product.user.uid) 55.dp else 0.dp),
                columns = GridCells.Fixed(2),
                content = {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .onGloballyPositioned { coordinates ->
                                    val offset = coordinates.positionInWindow()
                                    setFirstItemOffset(offset)
                                }
                        ) {
                            HorizontalPager(
                                pageCount = state.product.images.size,
                                state = pagerState,
                                key = { state.product.images[it] },
                                pageSize = PageSize.Fill,
                            ) { index ->
                                AsyncImage(
                                    model = state.product.images[index],
                                    contentDescription = "Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .shimmerEffect()
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(100))
                                    .background(MaterialTheme.colors.background.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = "${pagerState.currentPage + 1}/${state.product.images.size}",
                                    modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp)
                                )
                            }
                        }
                    }
                    item(span = { GridItemSpan(2) }) {
                        val listState = rememberLazyListState()
                        LaunchedEffect(pagerState.currentPage) {
                            listState.animateScrollToItem(pagerState.currentPage, -350)
                        }
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentPadding = PaddingValues(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            state = listState
                        ) {
                            itemsIndexed(state.product.images) { index, photoUrl ->
                                AsyncImage(
                                    model = photoUrl,
                                    contentDescription = "Image product",
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .aspectRatio(1f)
                                        .border(
                                            1.dp,
                                            if (pagerState.currentPage == index) Color.Red else Color.Transparent
                                        )
                                        .clickable(
                                            interactionSource = MutableInteractionSource(),
                                            indication = null,
                                            onClick = {
                                                scope.launch {
                                                    pagerState.animateScrollToPage(index)
                                                }
                                            }
                                        )
                                )
                            }
                        }
                    }
                    item(span = { GridItemSpan(2) }) {
                        Text(
                            text = state.product.name,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                        )
                    }
                    item(span = { GridItemSpan(2) }) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RatingBar(
                                modifier = Modifier.padding(start = 10.dp),
                                rating = state.product.getAvgRate(),
                                spaceBetween = 2.dp
                            )
                            Text(
                                text = String.format("%.1f", state.product.getAvgRate()),
                                modifier = Modifier.padding(start = 15.dp, end = 5.dp)
                            )
                            Text(
                                text = "|",
                                modifier = Modifier.padding(5.dp)
                            )
                            Text(
                                text = "${state.product.getSold()} sold",
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                    }
                    item(span = { GridItemSpan(2) }) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Price: ${decimalFormat.format(state.product.price)}đ",
                                color = Color.Red,
                                modifier = Modifier.padding(horizontal = 15.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 15.dp, vertical = 5.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(if (state.user.uid != state.product.user.uid) Color.Red else Color.Gray)
                                    .clickable(
                                        onClick = onClickAddToCart,
                                        enabled = state.user.uid != state.product.user.uid
                                    )
                            ) {
                                Text(
                                    text = "Add to Cart",
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                    item(span = { GridItemSpan(2) }) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .background(MaterialTheme.colors.surface)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = state.product.user.profilePicture,
                                    contentDescription = "Shop's Profile picture",
                                    modifier = Modifier
                                        .padding(horizontal = 15.dp, vertical = 5.dp)
                                        .size(55.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = state.product.user.name,
                                        fontSize = 18.sp,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.location),
                                            contentDescription = "Icon Location",
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = state.product.user.address,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(end = 15.dp),
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 15.dp, vertical = 10.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(MaterialTheme.colors.background)
                                        .border(1.dp, Color.Red, RoundedCornerShape(5.dp))
                                        .clickable(onClick = viewShop)
                                ) {
                                    Text(
                                        text = "View Shop",
                                        color = Color.Red,
                                        modifier = Modifier.padding(
                                            horizontal = 20.dp,
                                            vertical = 5.dp
                                        )
                                    )
                                }
                            }
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .background(MaterialTheme.colors.surface)
                            )
                        }
                    }
                    item(span = { GridItemSpan(2) }) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Shop's Product",
                                    modifier = Modifier.padding(
                                        horizontal = 15.dp,
                                        vertical = 10.dp
                                    )
                                )
                                Text(
                                    text = "See All >",
                                    color = Color.Red,
                                    modifier = Modifier
                                        .padding(horizontal = 15.dp, vertical = 10.dp)
                                        .clickable(
                                            interactionSource = MutableInteractionSource(),
                                            indication = null,
                                            onClick = viewShop
                                        )
                                )
                            }
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp),
                                contentPadding = PaddingValues(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                val showList = if (state.productOfShop.size <= 6) {
                                    state.productOfShop
                                } else {
                                    state.productOfShop.subList(0, 6)
                                }
                                items(showList) { product ->
                                    ItemProduct(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .aspectRatio(3 / 5f),
                                        product = product,
                                        onClick = {
                                            viewProduct(product)
                                        }
                                    )
                                }
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .aspectRatio(3 / 5f)
                                            .clickable(
                                                interactionSource = MutableInteractionSource(),
                                                indication = null,
                                                onClick = viewShop
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Box(
                                                modifier = Modifier.border(
                                                    2.dp,
                                                    Color.Red,
                                                    CircleShape
                                                )
                                            ) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.next),
                                                    contentDescription = "Icon Next",
                                                    tint = Color.Red,
                                                    modifier = Modifier
                                                        .padding(4.dp)
                                                        .size(25.dp)
                                                )
                                            }
                                            Text(
                                                text = "See More",
                                                color = Color.Red,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.padding(top = 5.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .background(MaterialTheme.colors.surface)
                            )
                        }
                    }
                    item(span = { GridItemSpan(2) }) {
                        Column {
                            Text(
                                text = "Category: ${state.product.category.toCategoryString()}",
                                modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Divider(modifier = Modifier.fillMaxWidth())
                            Text(
                                text = "Description:\n${state.product.description}",
                                modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                            )
                            Divider(modifier = Modifier.fillMaxWidth())
                            Text(
                                text = "Review: ${if (state.product.reviews.isEmpty()) "No Review" else state.product.reviews.size.toString()}",
                                modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                            )
                            state.product.reviews.forEach { review ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = review.user.profilePicture,
                                        contentDescription = "User's profile picture",
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .padding(start = 5.dp)
                                            .clip(CircleShape)
                                            .size(45.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                    Text(
                                        text = review.user.name,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                RatingBar(
                                    modifier = Modifier.padding(start = 65.dp),
                                    rating = review.rate.toFloat(),
                                    spaceBetween = 2.dp
                                )
                                Text(
                                    text = review.comment,
                                    modifier = Modifier.padding(start = 65.dp)
                                )
                                LazyRow(
                                    modifier = Modifier
                                        .padding(start = 55.dp)
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    contentPadding = PaddingValues(10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    items(review.images) { photoUrl ->
                                        AsyncImage(
                                            model = photoUrl,
                                            contentDescription = "Image review product",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(1f),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                            Divider(modifier = Modifier.fillMaxWidth())
                        }
                    }
                    item(span = { GridItemSpan(2) }) {
                        Text(
                            text = "Similar Products",
                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
                        )
                    }
                    items(state.similarProducts) { product ->
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
                    if (state.user.uid == state.product.user.uid) {
                        item(span = { GridItemSpan(2) }) {
                            Spacer(modifier = Modifier.navigationBarsPadding())
                        }
                    }
                }
            )
            ProductTopBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(),
                firstItemOffset = state.firstItemOffset,
                numberOfCart = state.numberOfCart,
                numberOfChats = state.numberOfChats,
                onBack = onBack,
                onClickCart = onClickCart,
                onClickChats = onClickChatIcon
            )
            if (state.user.uid != state.product.user.uid) {
                ProductBottomBar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    onClickChats = { onClickChatWithShop(state.product.user.uid) },
                    onClickAddToCart = onClickAddToCart,
                    onClickBuyNow = onClickBuyNow
                )
            }
        }
    }
    BackHandler(
        enabled = sheetState.isVisible,
        onBack = { setShowBottomSheet(false) }
    )
}