package com.sanryoo.shopping.feature.presentation.using.chats

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.sanryoo.shopping.R
import com.sanryoo.shopping.feature.domain.model.Message
import com.sanryoo.shopping.feature.domain.model.User
import com.sanryoo.shopping.feature.util.Screen
import com.sanryoo.shopping.ui.theme.Primary
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat

@Composable
fun ChatsScreen(
    navController: NavHostController,
    viewModel: ChatsViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is ChatsUIEvent.BackToPrevScreen -> {
                    navController.popBackStack()
                }

                is ChatsUIEvent.NavigateToMessage -> {
                    navController.navigate(Screen.Message.route + "?othersId=${event.otherId}")
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    ChatsContent(
        state = state,
        onBack = { viewModel.onUiEvent(ChatsUIEvent.BackToPrevScreen) },
        onGoToMessage = { viewModel.onUiEvent(ChatsUIEvent.NavigateToMessage(it)) }
    )
}

@Composable
private fun ChatsContent(
    state: ChatsState = ChatsState(),
    onBack: () -> Unit = {},
    onGoToMessage: (String) -> Unit = {}
) {
    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.onBackground,
                elevation = 4.dp,
            ) {
                Box(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "Icon back",
                        tint = Primary,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(10.dp)
                            .clip(CircleShape)
                            .size(30.dp)
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick = onBack
                            )
                    )
                    Text(
                        text = "Chats",
                        fontSize = 20.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    ) {
        LazyColumn {
            items(state.messages) { message ->
                ItemChats(
                    modifier = Modifier.fillMaxWidth(),
                    message = message,
                    user = state.user,
                    isRead = !message.read && message.to.uid == state.user.uid,
                    onClick = {
                        val othersId = if(state.user.uid == message.from.uid) {
                            message.to.uid
                        } else {
                            message.from.uid
                        }
                        onGoToMessage(othersId)
                    }
                )
                Divider(Modifier.fillMaxWidth())
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
private fun ItemChats(
    modifier: Modifier = Modifier,
    message: Message,
    user: User,
    isRead: Boolean = false,
    onClick: () -> Unit = {}
) {
    val simpleDateFormat = SimpleDateFormat("MMM dd")
    Row(
        modifier = modifier
            .height(80.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = if(user.uid == message.from.uid) message.to.profilePicture else message.from.profilePicture,
            contentDescription = "Profile picture of others",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .clip(CircleShape)
                .size(60.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(end = 10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if(user.uid == message.from.uid) message.to.name else message.from.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                if (isRead) {
                    Box(
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.Blue)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = message.text.ifBlank { "Sent ${message.images.size} images" },
                    maxLines = 1,
                    fontWeight = if (isRead) FontWeight.Medium else FontWeight.Normal,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = message.date?.let { " - ${simpleDateFormat.format(it)}" } ?: "",
                    maxLines = 1,
                    fontWeight = if (isRead) FontWeight.Medium else FontWeight.Normal,
                )
            }
        }
    }
}