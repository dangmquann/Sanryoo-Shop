package com.sanryoo.shopping.feature.presentation.using.edit_product.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sanryoo.shopping.R

@Composable
fun ItemEditProduct(
    modifier: Modifier = Modifier,
    icon: Int,
    label: String,
    value: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colors.background)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "Icon",
            modifier = Modifier
                .padding(15.dp)
                .size(25.dp)
        )
        Text(text = label, modifier = Modifier.padding(end = 10.dp))
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            maxLines = 1
        )
        Icon(
            painter = painterResource(id = R.drawable.next),
            contentDescription = "Icon next",
            tint = MaterialTheme.colors.onBackground,
            modifier = Modifier.size(25.dp)
        )
    }
}