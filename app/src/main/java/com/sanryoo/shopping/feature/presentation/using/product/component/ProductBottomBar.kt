package com.sanryoo.shopping.feature.presentation.using.product.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanryoo.shopping.R

@Composable
fun ProductBottomBar(
    modifier: Modifier = Modifier,
    onClickChats: () -> Unit = {},
    onClickAddToCart: () -> Unit = {},
    onClickBuyNow: () -> Unit = {},
) {
    Surface(
        modifier = modifier,
        elevation = 10.dp,
    ) {
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth()
                .height(55.dp)
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable(onClick = onClickChats),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.message),
                    contentDescription = "Icon Message",
                    modifier = Modifier
                        .padding(2.dp)
                        .size(22.dp),
                    tint = Color.Red
                )
                Text(text = "Chat now", fontSize = 14.sp)
            }
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight(0.7f)
                    .background(Color.Gray)
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable(onClick = onClickAddToCart),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.cart),
                    contentDescription = "Icon Cart",
                    modifier = Modifier
                        .padding(2.dp)
                        .size(22.dp),
                    tint = Color.Red
                )
                Text(text = "Add to Cart", fontSize = 14.sp)
            }
            Box(
                modifier = Modifier
                    .weight(1.7f)
                    .fillMaxHeight()
                    .background(Color.Red)
                    .clickable(onClick = onClickBuyNow),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Buy Now",
                    color = Color.White
                )
            }
        }
    }
}