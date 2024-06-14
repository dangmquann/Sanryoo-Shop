package com.sanryoo.shopping.feature.presentation.using.edit_product

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue.Expanded
import androidx.compose.material.ModalBottomSheetValue.HalfExpanded
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.sanryoo.shopping.R
import com.sanryoo.shopping.feature.domain.model.Product
import com.sanryoo.shopping.feature.domain.model.Stock
import com.sanryoo.shopping.feature.domain.model.Variation
import com.sanryoo.shopping.feature.presentation._component.bottomsheet.ChooseImages
import com.sanryoo.shopping.feature.presentation.using.edit_product.SheetContent.CATEGORY
import com.sanryoo.shopping.feature.presentation.using.edit_product.SheetContent.DEFAULT
import com.sanryoo.shopping.feature.presentation.using.edit_product.SheetContent.IMAGES
import com.sanryoo.shopping.feature.presentation.using.edit_product.SheetContent.STOCK
import com.sanryoo.shopping.feature.presentation.using.edit_product.SheetContent.VARIATIONS
import com.sanryoo.shopping.feature.presentation.using.edit_product.component.CustomTextField
import com.sanryoo.shopping.feature.presentation.using.edit_product.component.ItemEditProduct
import com.sanryoo.shopping.feature.presentation.using.edit_product.component.ItemProductPrice
import com.sanryoo.shopping.feature.presentation.using.edit_product.component.ProductImages
import com.sanryoo.shopping.feature.presentation.using.edit_product.component.bottomsheet.Category
import com.sanryoo.shopping.feature.presentation.using.edit_product.component.bottomsheet.Stock
import com.sanryoo.shopping.feature.presentation.using.edit_product.component.bottomsheet.Variation
import com.sanryoo.shopping.feature.util.ProductConstant
import com.sanryoo.shopping.feature.util.toCategoryString
import com.sanryoo.shopping.ui.theme.Primary
import kotlinx.coroutines.flow.collectLatest

@ExperimentalPermissionsApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun EditProductScreen(
    product: Product,
    label: String,
    button: String,
    navController: NavHostController,
    scaffoldState: ScaffoldState,
    viewModel: EditProductViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val sheetState = rememberModalBottomSheetState(
        initialValue = Hidden,
        animationSpec = tween(500),
        confirmStateChange = { it != HalfExpanded }
    )
    LaunchedEffect(Unit) {
        viewModel.setInitProduct(product)
    }
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is EditProductUiEvent.BackToShop -> {
                    navController.popBackStack()
                }

                is EditProductUiEvent.ClearFocus -> {
                    focusManager.clearFocus()
                }

                is EditProductUiEvent.SetShowBottomSheet -> {
                    sheetState.animateTo(if (event.status) Expanded else Hidden)
                }

                is EditProductUiEvent.ShowSnackBar -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    EditProductContent(
        label = label,
        button = button,
        sheetState = sheetState,
        state = state,
        onChangeExistImages = viewModel::onChangeExistImages,
        onChangeImages = viewModel::onChangeImages,
        onChangeName = viewModel::onChangeName,
        onChangeDescription = viewModel::onChangeDescription,
        onChangePrice = viewModel::onChangePrice,
        onChangeCategory = viewModel::onChangeCategory,
        onChangeVariation = viewModel::onChangeVariations,
        setStocks = viewModel::setStocks,
        onAdd = viewModel::onAdd,
        setSheetContent = viewModel::setSheetContent,
        onBack = { viewModel.onUiEvent(EditProductUiEvent.BackToShop) },
        onClearFocus = { viewModel.onUiEvent(EditProductUiEvent.ClearFocus) },
        setShowBottomSheet = { viewModel.onUiEvent(EditProductUiEvent.SetShowBottomSheet(it)) },
    )
}

@ExperimentalPermissionsApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
private fun EditProductContent(
    label: String,
    button: String,
    sheetState: ModalBottomSheetState,
    state: EditProductState = EditProductState(),
    onChangeExistImages: (List<String>) -> Unit = {},
    onChangeImages: (List<Uri>) -> Unit = {},
    onChangeName: (String) -> Unit = {},
    onChangeDescription: (String) -> Unit = {},
    onChangePrice: (Long) -> Unit = {},
    onChangeCategory: (List<String>) -> Unit = {},
    onChangeVariation: (List<Variation>) -> Unit = {},
    setStocks: (List<Stock>) -> Unit = {},
    onAdd: () -> Unit = {},
    setSheetContent: (SheetContent) -> Unit = {},
    onBack: () -> Unit = {},
    onClearFocus: () -> Unit = {},
    setShowBottomSheet: (Boolean) -> Unit = {},
) {
    ModalBottomSheetLayout(
        modifier = Modifier.statusBarsPadding(),
        sheetElevation = 0.dp,
        sheetState = sheetState,
        sheetBackgroundColor = MaterialTheme.colors.background,
        sheetContentColor = MaterialTheme.colors.onBackground,
        sheetContent = {
            when (state.sheetContent) {
                DEFAULT -> {
                    Box(modifier = Modifier.fillMaxSize())
                }

                IMAGES -> {
                    ChooseImages(
                        currentImages = state.images,
                        currentSize = state.product.images.size + state.images.size,
                        hideBottomSheet = { setShowBottomSheet(false) },
                        onChangeImages = onChangeImages,
                    )
                }

                CATEGORY -> {
                    Category(
                        categories = state.categories,
                        currentCategory = state.currentCategory,
                        category = state.product.category,
                        setCategory = onChangeCategory,
                        hideBottomSheet = { setShowBottomSheet(false) }
                    )
                }

                VARIATIONS -> {
                    Variation(
                        variations = state.product.variations,
                        hideBottomSheet = { setShowBottomSheet(false) },
                        onChangeVariation = onChangeVariation,
                    )
                }

                STOCK -> {
                    Stock(
                        variations = state.product.variations,
                        hideBottomSheet = { setShowBottomSheet(false) },
                        stocks = state.product.stocks,
                        setStocks = setStocks
                    )
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                    onClick = onClearFocus
                ),
            topBar = {
                Surface(modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                    elevation = 15.dp,
                    color = MaterialTheme.colors.background,
                    contentColor = MaterialTheme.colors.onBackground,
                    content = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.back),
                                contentDescription = "Icon back",
                                tint = Primary,
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 5.dp)
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .clickable(interactionSource = MutableInteractionSource(),
                                        indication = null,
                                        onClick = {
                                            onClearFocus()
                                            onBack()
                                        })
                            )
                            Text(
                                text = label,
                                fontSize = 24.sp,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                )
            }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(MaterialTheme.colors.surface)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.background),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.image),
                            contentDescription = "Icon image",
                            modifier = Modifier
                                .padding(15.dp)
                                .size(25.dp)
                        )
                        Text(text = "Image(${state.images.size + state.product.images.size}/9): ")
                    }
                    ProductImages(
                        modifier = Modifier.fillMaxWidth(),
                        existImages = state.product.images,
                        onChangeExistImages = onChangeExistImages,
                        images = state.images,
                        onChangeImages = onChangeImages,
                        setSheetContent = setSheetContent,
                        setShowBottomSheet = setShowBottomSheet,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    CustomTextField(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Product Name",
                        hint = "Enter Product Name",
                        value = state.product.name,
                        onValueChange = onChangeName,
                        maxLength = ProductConstant.MAX_NAME,
                        imeAction = ImeAction.Next
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    CustomTextField(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Product Description",
                        hint = "Enter Product Description",
                        value = state.product.description,
                        onValueChange = onChangeDescription,
                        maxLength = ProductConstant.MAX_DESCRIPTION,
                        imeAction = ImeAction.Default
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ItemEditProduct(
                        modifier = Modifier.fillMaxWidth(),
                        icon = R.drawable.categories,
                        label = "Category: ",
                        value = state.product.category.toCategoryString(),
                        onClick = {
                            onClearFocus()
                            setSheetContent(CATEGORY)
                            setShowBottomSheet(true)
                        }
                    )
                    Divider(modifier = Modifier.fillMaxWidth())
                    ItemEditProduct(
                        modifier = Modifier.fillMaxWidth(),
                        icon = R.drawable.variations,
                        label = "Variation",
                        value = "",
                        onClick = {
                            onClearFocus()
                            setSheetContent(VARIATIONS)
                            setShowBottomSheet(true)
                        }
                    )
                    Divider(modifier = Modifier.fillMaxWidth())
                    ItemEditProduct(
                        modifier = Modifier.fillMaxWidth(),
                        icon = R.drawable.stock,
                        label = "Stock",
                        value = "",
                        onClick = {
                            onClearFocus()
                            setSheetContent(STOCK)
                            setShowBottomSheet(true)
                        }
                    )
                    Divider(modifier = Modifier.fillMaxWidth())
                    ItemProductPrice(
                        price = state.product.price,
                        onChangePrice = onChangePrice
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                Surface(
                    elevation = 15.dp,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colors.background,
                    contentColor = MaterialTheme.colors.onBackground,
                    content = {
                        Button(
                            onClick = onAdd,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    color = Color.White,
                                    modifier = Modifier.size(25.dp)
                                )
                            } else {
                                Text(text = button)
                            }
                        }
                    }
                )
            }
        }
    }
    BackHandler(
        enabled = sheetState.isVisible,
        onBack = { setShowBottomSheet(false) }
    )
}