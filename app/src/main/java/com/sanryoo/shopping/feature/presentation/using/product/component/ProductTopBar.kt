package com.sanryoo.shopping.feature.presentation.using.product.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanryoo.shopping.R

@Composable
fun ProductTopBar(
    modifier: Modifier = Modifier,
    firstItemOffset: Offset = Offset.Zero,
    numberOfCart: Int = 0,
    numberOfChats: Int = 0,
    onBack: () -> Unit = {},
    onClickCart: () -> Unit = {},
    onClickChats: () -> Unit = {},
) {
    var backgroundColor by remember {
        mutableStateOf(Color.Transparent)
    }
    var secondBackgroundColor by remember {
        mutableStateOf(Color.Black.copy(alpha = 0.2f))
    }
    var contentColor by remember {
        mutableStateOf(Color.White)
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
    Card(
        modifier = modifier,
        backgroundColor = backgroundColor,
        elevation = if(-firstItemOffset.y >= 400f) 16.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .height(58.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 3.dp)
                    .clip(CircleShape)
                    .background(secondBackgroundColor)
                    .clickable(onClick = onBack)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Icon back",
                    tint = contentColor,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(30.dp),
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .background(secondBackgroundColor, CircleShape)
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
                    tint = contentColor
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
                    .background(secondBackgroundColor, CircleShape)
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
                    tint = contentColor
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
}