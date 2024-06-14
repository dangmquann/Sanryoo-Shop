package com.sanryoo.shopping.feature.presentation.using.home

import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.sanryoo.shopping.feature.domain.model.Product
import com.sanryoo.shopping.feature.presentation._component.ItemProduct
import com.sanryoo.shopping.feature.presentation._component.shimmerEffect
import com.sanryoo.shopping.feature.presentation.using.home.component.ChooseCategory
import com.sanryoo.shopping.feature.presentation.using.home.component.HomeTopBar
import com.sanryoo.shopping.feature.util.Screen
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@FlowPreview
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    navController: NavHostController,
    scaffoldState: ScaffoldState,
    viewModel: HomeViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is HomeUiEvent.NavigateToLogIn -> {
                    navController.navigate(Screen.LogIn.route)
                }

                is HomeUiEvent.NavigateToCart -> {
                    navController.navigate(Screen.Cart.route)
                }

                is HomeUiEvent.NavigateToChats -> {
                    navController.navigate(Screen.Chats.route)
                }

                is HomeUiEvent.NavigateToProduct -> {
                    val productJson = Gson().toJson(event.product)
                    val encodedJson = Uri.encode(productJson)
                    navController.navigate(Screen.Product.route + "?product=$encodedJson")
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    HomeContent(
        state = state,
        setCurrentCategory = viewModel::setCurrentCategory,
        onSearchTextChange = viewModel::onSearchTextChange,
        onClickCart = viewModel::onClickCart,
        onClickChats = viewModel::onClickChats,
        onClickProduct = { viewModel.onUiEvent(HomeUiEvent.NavigateToProduct(it)) },
    )
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun HomeContent(
    state: HomeState = HomeState(),
    setCurrentCategory: (String) -> Unit = {},
    onSearchTextChange: (String) -> Unit = {},
    onClickCart: () -> Unit = {},
    onClickChats: () -> Unit = {},
    onClickProduct: (Product) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val pagerState = rememberPagerState()
    var firstItemOffset by remember {
        mutableStateOf(Offset.Zero)
    }
    val lazyGridState = rememberLazyGridState()
    LaunchedEffect(lazyGridState.isScrollInProgress) {
        if (lazyGridState.isScrollInProgress) {
            focusManager.clearFocus()
        }
    }
    LaunchedEffect(state.eventImages) {
        while (true) {
            delay(4000)
            if (state.eventImages.size >= 2) {
                var nextImage = pagerState.currentPage + 1
                if (nextImage >= state.eventImages.size)
                    nextImage = 0
                pagerState.animateScrollToPage(nextImage, animationSpec = tween(400))
            }
        }
    }
    Box(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxSize()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = { focusManager.clearFocus() }
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                state = lazyGridState
            ) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .onGloballyPositioned { coordinates ->
                                val offset = coordinates.positionInWindow()
                                firstItemOffset = offset
                            }
                    ) {
                        HorizontalPager(
                            pageCount = state.eventImages.size,
                            state = pagerState,
                            key = { state.eventImages[it] },
                            pageSize = PageSize.Fill,
                        ) { index ->
                            AsyncImage(
                                model = state.eventImages[index],
                                contentDescription = "Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .shimmerEffect()
                            )
                        }
                        Row(modifier = Modifier.align(Alignment.BottomCenter)) {
                            state.eventImages.forEachIndexed { index, _ ->
                                Box(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (index == pagerState.currentPage) {
                                                Color.White
                                            } else {
                                                Color.White.copy(alpha = 0.3f)
                                            }
                                        )
                                )
                            }
                        }
                    }
                }
                item(span = { GridItemSpan(2) }) {
                    ChooseCategory(
                        categories = state.categories,
                        setCurrentCategory = setCurrentCategory
                    )
                }
                items(state.showingProduct, key = { it.pid }) { product ->
                    ItemProduct(
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth()
                            .aspectRatio(3 / 5f)
                            .animateItemPlacement(tween(500)),
                        product = product,
                        onClick = {
                            focusManager.clearFocus()
                            onClickProduct(product)
                        }
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
        }
        HomeTopBar(
            modifier = Modifier.fillMaxSize(),
            firstItemOffset = firstItemOffset,
            searchText = state.searchText,
            searchResult = state.searchResult,
            numberOfCart = state.numberOfCart,
            numberOfChats = state.numberOfChats,
            clearFocus = { focusManager.clearFocus() },
            onSearchTextChange = onSearchTextChange,
            onClickCart = onClickCart,
            onClickChats = onClickChats,
            onClickSearchItem = onClickProduct,
        )
    }
}