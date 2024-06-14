package com.sanryoo.shopping.feature.presentation.using.review

import android.net.Uri
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.sanryoo.shopping.R
import com.sanryoo.shopping.feature.domain.model.Order
import com.sanryoo.shopping.feature.presentation._component.bottomsheet.ChooseImages
import com.sanryoo.shopping.feature.presentation._component.readImagePermission
import com.sanryoo.shopping.feature.presentation.using.edit_product.component.CustomTextField
import com.sanryoo.shopping.feature.util.ProductConstant
import com.sanryoo.shopping.feature.util.decimalFormat
import com.sanryoo.shopping.feature.util.toCategoryString
import com.sanryoo.shopping.ui.theme.Primary
import kotlinx.coroutines.flow.collectLatest

@ExperimentalMaterialApi
@ExperimentalPermissionsApi
@Composable
fun ReviewScreen(
    order: Order,
    navController: NavHostController,
    scaffoldState: ScaffoldState,
    viewModel: ReviewViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val sheetState = rememberModalBottomSheetState(
        initialValue = Hidden,
        animationSpec = tween(500),
        confirmStateChange = { it != HalfExpanded }
    )
    LaunchedEffect(Unit) {
        viewModel.setOrder(order)
    }
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is ReviewUIEvent.ClearFocus -> {
                    focusManager.clearFocus()
                }

                is ReviewUIEvent.ShowSnackBar -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.message)
                }

                is ReviewUIEvent.BackToPrevScreen -> {
                    navController.popBackStack()
                }

                is ReviewUIEvent.SetShowBottomSheet -> {
                    sheetState.animateTo(if (event.status) Expanded else Hidden)
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    ReviewContent(
        sheetState = sheetState,
        state = state,
        onRateChange = viewModel::onRateChange,
        onCommentChange = viewModel::onCommentChange,
        onListImageCommentChange = viewModel::onListImageCommentChange,
        onSendReview = viewModel::sendReview,
        setShowBottomSheet = { viewModel.onUiEvent(ReviewUIEvent.SetShowBottomSheet(it)) },
        clearFocus = { viewModel.onUiEvent(ReviewUIEvent.ClearFocus) },
        onBack = { viewModel.onUiEvent(ReviewUIEvent.BackToPrevScreen) }
    )
}

@ExperimentalMaterialApi
@ExperimentalPermissionsApi
@Composable
private fun ReviewContent(
    sheetState: ModalBottomSheetState,
    state: ReviewState = ReviewState(),
    onRateChange: (Int) -> Unit = {},
    onCommentChange: (String) -> Unit = {},
    onListImageCommentChange: (List<Uri>) -> Unit = {},
    onSendReview: () -> Unit = {},
    setShowBottomSheet: (Boolean) -> Unit = {},
    clearFocus: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val readImagePermission = readImagePermission(
        onPermissionGranted = {
            setShowBottomSheet(true)
        }
    )
    ModalBottomSheetLayout(
        modifier = Modifier
            .statusBarsPadding()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = clearFocus
            ),
        sheetState = sheetState,
        sheetElevation = 0.dp,
        sheetContent = {
            ChooseImages(
                hideBottomSheet = { setShowBottomSheet(false) },
                maxImages = ProductConstant.MAX_IMAGES_REVIEW,
                currentSize = state.listImagesComment.size,
                currentImages = state.listImagesComment,
                onChangeImages = onListImageCommentChange
            )
        }
    ) {
        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "Review Product",
                        fontSize = 20.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "Icon Back",
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 10.dp)
                            .size(35.dp)
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick = onBack
                            )
                    )
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = if (state.order.product.images.isNotEmpty()) state.order.product.images[0] else "",
                        contentDescription = "First Product's image",
                        modifier = Modifier
                            .padding(10.dp)
                            .size(120.dp)
                    )
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = state.order.product.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(5.dp)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "Category: " + state.order.product.category.toCategoryString(),
                            modifier = Modifier.padding(5.dp)
                        )
                        Text(
                            text = "Description:\n${state.order.product.description}",
                            modifier = Modifier.padding(5.dp)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "Price: ${decimalFormat.format(state.order.product.price)}đ",
                            color = Color.Red,
                            modifier = Modifier.padding(5.dp),
                            fontSize = 18.sp
                        )
                    }
                }
                Divider(Modifier.fillMaxWidth())
                Text(
                    text = "Rate: ",
                    modifier = Modifier
                        .padding(5.dp)
                        .padding(start = 10.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    (1..5).forEach { index ->
                        Icon(
                            painter = painterResource(
                                id = if (index <= state.rate) R.drawable.star_full else R.drawable.star
                            ),
                            contentDescription = "Icon star",
                            tint = Primary,
                            modifier = Modifier
                                .padding(vertical = 15.dp)
                                .size(30.dp)
                                .clickable(
                                    interactionSource = MutableInteractionSource(),
                                    indication = null,
                                    onClick = { onRateChange(index) }
                                )
                        )
                    }
                }
                Divider(Modifier.fillMaxWidth())
                Text(
                    text = "Image: ", modifier = Modifier
                        .padding(5.dp)
                        .padding(start = 10.dp)
                )
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentPadding = PaddingValues(5.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    itemsIndexed(state.listImagesComment) { index, photoUrl ->
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(1f)
                        ) {
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = "Image comment",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colors.background)
                                    .border(1.dp, Color.Red, CircleShape)
                                    .clickable(interactionSource = MutableInteractionSource(),
                                        indication = null,
                                        onClick = {
                                            val tempList = state.listImagesComment.toMutableList()
                                            tempList.removeAt(index)
                                            onListImageCommentChange(tempList)
                                        }
                                    )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.close),
                                    contentDescription = "Icon close",
                                    tint = Color.Red,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .size(15.dp)
                                )
                            }
                        }
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(5.dp))
                                .border(
                                    width = 1.dp, color = Primary, shape = RoundedCornerShape(5.dp)
                                )
                                .clickable(interactionSource = MutableInteractionSource(),
                                    indication = null,
                                    onClick = {
                                        if (readImagePermission.status.isGranted) {
                                            setShowBottomSheet(true)
                                        } else {
                                            readImagePermission.launchPermissionRequest()
                                        }
                                    }), contentAlignment = Alignment.Center
                        ) {
                            Text(text = "+ Add", color = Primary)
                        }
                    }
                }
                Divider(Modifier.fillMaxWidth())
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Comment: ",
                    hint = "Please leave a message",
                    value = state.comment,
                    onValueChange = onCommentChange,
                    maxLength = ProductConstant.MAX_COMMENT
                )
                Divider(Modifier.fillMaxWidth())
                Button(
                    onClick = onSendReview,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    enabled = state.rate > 0 && state.listImagesComment.isNotEmpty() && state.comment.length >= 10
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            strokeWidth = 3.dp,
                            color = Color.White,
                            modifier = Modifier.size(35.dp)
                        )
                    } else {
                        Text(text = "Send review")
                    }
                }
            }
        }
    }
}