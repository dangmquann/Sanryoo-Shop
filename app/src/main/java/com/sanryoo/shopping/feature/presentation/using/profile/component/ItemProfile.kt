package com.sanryoo.shopping.feature.presentation.using.profile.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sanryoo.shopping.R

@Composable
fun ItemProfile(
    modifier: Modifier = Modifier,
    icon: Int,
    label: String,
    enable: Boolean = true,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(onClick = onClick, enabled = enable),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "Icon",
            modifier = Modifier
                .size(45.dp)
                .padding(horizontal = 10.dp)
        )
        Text(
            text = label,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            maxLines = 1
        )
        Image(
            painter = painterResource(id = R.drawable.next),
            contentDescription = "Icon Next",
            modifier = Modifier
                .size(45.dp)
                .padding(horizontal = 5.dp)
        )
    }
}