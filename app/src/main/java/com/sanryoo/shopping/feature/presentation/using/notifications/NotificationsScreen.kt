package com.sanryoo.shopping.feature.presentation.using.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.sanryoo.shopping.R
import com.sanryoo.shopping.feature.domain.model.notification.Notification
import com.sanryoo.shopping.feature.presentation.using.notifications.component.ItemNotifications
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NotificationScreen(
    navController: NavHostController,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when(event) {
                is NotificationsUIEvent.NavigateToRoute -> {
                    navController.navigate(event.route)
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    NotificationContent(
        state = state,
        markAllAsRead = viewModel::markAllAsRead,
        onClickItem = viewModel::onClickNotification
    )
}

@Composable
private fun NotificationContent(
    state: NotificationState = NotificationState(),
    markAllAsRead: () -> Unit = {},
    onClickItem: (Notification) -> Unit = {}
) {
    Scaffold(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(bottom = 50.dp),
        topBar = {
            Surface(
                color = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.onBackground,
                elevation = 8.dp,
            ) {
                Box(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "Notifications",
                        fontSize = 22.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.double_tick),
                        contentDescription = "Icon Double Tick",
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 10.dp)
                            .size(30.dp)
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick = markAllAsRead
                            )
                    )
                }
            }
        }
    ) {
        LazyColumn {
            items(state.notifications) { notification ->
                ItemNotifications(
                    modifier = Modifier.fillMaxWidth(),
                    notification = notification,
                    onClick = onClickItem
                )
                Divider(Modifier.fillMaxWidth())
            }
        }
    }
}