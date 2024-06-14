package com.sanryoo.shopping.feature.presentation.using.notifications.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sanryoo.shopping.feature.domain.model.notification.Notification
import com.sanryoo.shopping.feature.util.dateFormat
import java.util.concurrent.TimeUnit

@Composable
fun ItemNotifications(
    modifier: Modifier = Modifier,
    notification: Notification,
    onClick: (Notification) -> Unit = {}
) {
    Row(
        modifier = modifier
            .background(if (notification.read) Color.White else Color(0xFFAFEAFF))
            .clickable { onClick(notification) }
    ) {
        AsyncImage(
            model = notification.image,
            contentDescription = "Notification Image",
            modifier = Modifier
                .padding(10.dp)
                .size(80.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(10.dp)
        ) {
            Text(
                text = notification.title,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = notification.message,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(5.dp))
            notification.date?.let { date ->
                val time = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - date.time)
                Text(
                    text = when {
                        time < 60 -> "$time minutes ago"
                        time <= 60 * 24 -> "${time / 60L} hours ago"
                        else -> dateFormat.format(date)
                    },
                    fontSize = 14.sp
                )
            }
        }
    }
}