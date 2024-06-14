package com.sanryoo.shopping.feature.presentation.using.my_account.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.sanryoo.shopping.ui.theme.Inactive

@Composable
fun ItemUser(
    modifier: Modifier = Modifier,
    label: String = "",
    content: String = "",
    showIconNext: Boolean = true,
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
        Text(
            text = label,
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = content.ifBlank { "Set Now" },
            color = if(content.isBlank()) Inactive else MaterialTheme.colors.onBackground,
            modifier = if(showIconNext) Modifier.weight(1f) else Modifier.padding(horizontal = 10.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End
        )
        if (showIconNext) {
            Image(
                painter = painterResource(id = R.drawable.next),
                contentDescription = "Icon Next",
                modifier = Modifier
                    .size(45.dp)
                    .padding(horizontal = 5.dp),
            )
        }
    }
}