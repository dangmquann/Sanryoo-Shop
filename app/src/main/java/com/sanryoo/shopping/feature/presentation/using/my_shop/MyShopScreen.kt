package com.sanryoo.shopping.feature.presentation.using.my_shop

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.ui.layout.ContentScale
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
import com.sanryoo.shopping.feature.presentation._component.shimmerEffect
import com.sanryoo.shopping.feature.presentation._component.ItemProduct
import com.sanryoo.shopping.feature.presentation.using.profile.ProfileUiEvent
import com.sanryoo.shopping.feature.util.Screen
import com.sanryoo.shopping.ui.theme.Primary
import kotlinx.coroutines.flow.collectLatest

@ExperimentalMaterialApi
@Composable
fun MyShopScreen(
    navController: NavHostController,
    scaffoldState: ScaffoldState,
    viewModel: MyShopViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is MyShopUiEvent.ShowSnackBar -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.message)
                }

                is MyShopUiEvent.BackToProfile -> {
                    navController.popBackStack()
                }

                is MyShopUiEvent.AddProduct -> {
                    val jsonProduct = Gson().toJson(Product())
                    navController.navigate(
                        Screen.EditProduct.route +
                                "?product=$jsonProduct" +
                                "&label=Add Product" +
                                "&button=Publish"
                    )
                }

                is MyShopUiEvent.EditProduct -> {
                    val jsonProduct = Gson().toJson(event.product)
                    val encodeJson = Uri.encode(jsonProduct)
                    navController.navigate(
                        Screen.EditProduct.route +
                                "?product=$encodeJson" +
                                "&label=Edit Product" +
                                "&button=Confirm"
                    )
                }

                is MyShopUiEvent.NavigateToMyShopPurchases -> {
                    navController.navigate(Screen.MyShopPurchases.route)
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    MyShopContent(
        state = state,
        onDeleteProduct = viewModel::deleteProduct,
        editProduct = { viewModel.onUiEvent(MyShopUiEvent.EditProduct(it)) },
        onAddProduct = { viewModel.onUiEvent(MyShopUiEvent.AddProduct) },
        onClickMyShopPurchases = {viewModel.onUiEvent(MyShopUiEvent.NavigateToMyShopPurchases)},
        onBack = { viewModel.onUiEvent(MyShopUiEvent.BackToProfile) },
    )
}

@ExperimentalMaterialApi
@Composable
private fun MyShopContent(
    state: MyShopState = MyShopState(),
    onDeleteProduct: (Product) -> Unit = {},
    editProduct: (Product) -> Unit = {},
    onAddProduct: () -> Unit = {},
    onClickMyShopPurchases: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .background(Color(0xFFFCD191))
                    .height(55.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable(onClick = onClickMyShopPurchases),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "My Shop's Purchase")
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(Primary)
                        .clickable(onClick = onAddProduct),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Add New Product", color = Color.White)
                }
            }
        }
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(bottom = 55.dp),
            columns = GridCells.Fixed(2),
            content = {
                item(span = { GridItemSpan(2) }) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        ) {
                            AsyncImage(
                                model = state.user.coverPhoto,
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
                                    model = state.user.profilePicture.ifBlank { R.drawable.user },
                                    contentDescription = "Profile picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(110.dp)
                                        .clip(CircleShape)
                                        .shimmerEffect()
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .statusBarsPadding()
                                    .align(Alignment.TopStart)
                                    .padding(5.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colors.background.copy(alpha = 0.3f))
                                    .clickable(onClick = onBack)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.back),
                                    contentDescription = "Icon back",
                                    tint = Primary,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .size(30.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = state.user.name,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                        Text(
                            text = "Bio: ${state.user.bio}",
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                        Text(
                            text = "Address: ${state.user.address}",
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
                items(state.products) { product ->
                    Surface(
                        elevation = 4.dp,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            ItemProduct(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(3 / 5f),
                                product = product,
                                enableClick = false,
                                elevation = 0.dp
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFCA11A))
                                    .clickable { editProduct(product) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Edit",
                                    modifier = Modifier.padding(10.dp),
                                    color = Color.White,
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF73030))
                                    .clickable { onDeleteProduct(product) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Delete",
                                    modifier = Modifier.padding(10.dp),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        )
    }
}