package com.sanryoo.shopping.feature.presentation.using.home.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sanryoo.shopping.R
import com.sanryoo.shopping.feature.domain.model.Product
import com.sanryoo.shopping.feature.domain.model.getAvgRate
import com.sanryoo.shopping.feature.domain.model.getSold
import com.sanryoo.shopping.feature.presentation.using.cart.component.customToString
import com.sanryoo.shopping.feature.presentation.using.product.component.RatingBar
import com.sanryoo.shopping.feature.util.decimalFormat
import com.sanryoo.shopping.ui.theme.Primary

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun HomeTopBar(
    modifier: Modifier = Modifier,
    firstItemOffset: Offset = Offset.Zero,
    searchText: String = "",
    searchResult: List<Product> = emptyList(),
    numberOfCart: Int = 0,
    numberOfChats: Int = 0,
    onSearchTextChange: (String) -> Unit = {},
    clearFocus: () -> Unit = {},
    onClickCart: () -> Unit = {},
    onClickChats: () -> Unit = {},
    onClickSearchItem: (Product) -> Unit = {}
) {
    var isFocusing by remember {
        mutableStateOf(false)
    }
    var backgroundColor by remember {
        mutableStateOf(Color.Transparent)
    }
    var secondBackgroundColor by remember {
        mutableStateOf(Color.Black.copy(alpha = 0.2f))
    }
    var contentColor by remember {
        mutableStateOf(Color.White)
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val lazyListState = rememberLazyListState()
    LaunchedEffect(lazyListState.isScrollInProgress) {
        keyboardController?.hide()
    }

    LaunchedEffect(firstItemOffset) {
        val alpha = if (-firstItemOffset.y >= 400) 1f else -firstItemOffset.y / 400f

        backgroundColor = Color.White.copy(alpha = alpha)
        secondBackgroundColor = Color.Black.copy(alpha = if (alpha < 0.2f) 0.2f - alpha else 0f)
        contentColor = Color.White.copy(
            red = 1f - alpha,
            green = 1f - alpha,
            blue = 1f - alpha
        )
    }
    Column(modifier = modifier) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = if (isFocusing) Color.White else backgroundColor,
            elevation = if (-firstItemOffset.y >= 400f) 16.dp else 0.dp
        ) {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .height(58.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(visible = isFocusing) {
                    Box(
                        modifier = Modifier
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick = clearFocus
                            )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back icon",
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .size(30.dp),
                            tint = Color.Black
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(5.dp)
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(100))
                        .background(if (isFocusing) Color.White else secondBackgroundColor)
                        .border(
                            1.dp,
                            if (isFocusing) Color.Black else contentColor.copy(alpha = 0.4f),
                            RoundedCornerShape(100)
                        )
                ) {
                    BasicTextField(
                        value = searchText,
                        onValueChange = onSearchTextChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterStart)
                            .padding(horizontal = 15.dp)
                            .onFocusChanged {
                                isFocusing = it.isFocused
                            },
                        textStyle = TextStyle(
                            color = if (isFocusing) Color.Black else contentColor,
                            fontSize = 16.sp
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(Primary),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() })
                    )
                    if (searchText.isEmpty()) {
                        Text(
                            text = "Search",
                            color = if (isFocusing) Color.Black.copy(alpha = 0.4f) else contentColor.copy(
                                alpha = 0.4f
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 15.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .background(
                            if (isFocusing) Color.White else secondBackgroundColor,
                            CircleShape
                        )
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = onClickCart
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.cart),
                        contentDescription = "Cart icon",
                        modifier = Modifier
                            .padding(10.dp)
                            .size(30.dp),
                        tint = if (isFocusing) Color.Black else contentColor
                    )
                    if (numberOfCart > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color.Red, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = numberOfCart.toString(),
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .background(
                            if (isFocusing) Color.White else secondBackgroundColor,
                            CircleShape
                        )
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = onClickChats
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.message),
                        contentDescription = "Message icon",
                        modifier = Modifier
                            .padding(10.dp)
                            .size(30.dp),
                        tint = if (isFocusing) Color.Black else contentColor
                    )
                    if (numberOfChats > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color.Red, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = numberOfChats.toString(),
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
        AnimatedVisibility(visible = isFocusing) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = { keyboardController?.hide() }
                    )
            ) {
                items(searchResult, key = { it.pid }) { product ->
                    SearchItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItemPlacement(tween(500)),
                        product = product,
                        onClick = { onClickSearchItem(product) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchItem(
    modifier: Modifier = Modifier,
    product: Product = Product(),
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .height(120.dp)
            .background(Color.White)
            .clickable(onClick = onClick),
    ) {
        AsyncImage(
            model = product.images[0],
            contentDescription = "image",
            modifier = Modifier
                .padding(10.dp)
                .size(100.dp),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = product.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "Cost: ${decimalFormat.format(product.price)}đ",
                color = Color.Red,
                fontSize = 18.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RatingBar(
                    rating = product.getAvgRate(),
                    spaceBetween = 4.dp,
                    modifier = Modifier.padding(horizontal = 5.dp)
                )
                Text(
                    text = "Sold: ${product.getSold()}",
                )
            }
            Text(text = "${product.reviews.size} reviews", Modifier.padding(horizontal = 5.dp))
        }
    }
}