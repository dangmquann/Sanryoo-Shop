package com.sanryoo.shopping.feature.presentation._component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sanryoo.shopping.feature.domain.model.Product
import com.sanryoo.shopping.feature.domain.model.getSold
import com.sanryoo.shopping.feature.util.decimalFormat
import java.text.DecimalFormat

@ExperimentalMaterialApi
@Composable
fun ItemProduct(
    modifier: Modifier = Modifier,
    elevation: Dp = 4.dp,
    product: Product = Product(),
    enableClick: Boolean = true,
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier,
        elevation = elevation,
        color = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.onBackground,
        enabled = enableClick,
        onClick = onClick
    ) {
        Column() {
            AsyncImage(
                model = if (product.images.isNotEmpty()) product.images[0] else "",
                contentDescription = "First item image",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shimmerEffect(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .align(Alignment.CenterStart),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = try {
                        if (product.price >= 0) {
                            decimalFormat.format(product.price) + "đ"
                        } else {
                            "-"
                        }
                    } catch (_: Exception) {
                        "-"
                    },
                    color = Color.Red,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = "${product.getSold()} sold",
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}