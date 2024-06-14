package com.sanryoo.shopping.feature.presentation.using.my_likes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.sanryoo.shopping.R
import com.sanryoo.shopping.feature.domain.model.Like
import com.sanryoo.shopping.feature.domain.model.User
import com.sanryoo.shopping.feature.util.Screen
import com.sanryoo.shopping.ui.theme.Primary
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MyLikesScreen(
    navController: NavHostController,
    viewModel: MyLikesViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is MyLikesUiEvent.ViewShop -> {
                    navController.navigate(Screen.Shop.route + "?uid=${event.uid}")
                }

                is MyLikesUiEvent.BackToPrevScreen -> {
                    navController.popBackStack()
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    MyLikesContent(
        state = state,
        onUnlikeShop = viewModel::unlikeShop,
        viewShop = { viewModel.onUiEvent(MyLikesUiEvent.ViewShop(it.uid)) },
        onBack = { viewModel.onUiEvent(MyLikesUiEvent.BackToPrevScreen) }
    )
}

@Composable
private fun MyLikesContent(
    state: MyLikesState = MyLikesState(),
    onUnlikeShop: (Like) -> Unit = {},
    viewShop: (User) -> Unit = {},
    onBack: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.onBackground,
                elevation = 16.dp,
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
                        text = "My Liked Shop",
                        fontSize = 20.sp,
                        color = Primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            if (state.likes.isEmpty()) {
                item {
                    Text(
                        text = "You have not liked any shops yet",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp, vertical = 10.dp),
                    )
                }
            }
            items(state.likes) { like ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewShop(like.user)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = like.user.profilePicture,
                        contentDescription = "Shop's Profile picture",
                        modifier = Modifier
                            .padding(horizontal = 15.dp, vertical = 8.dp)
                            .size(55.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = like.user.name,
                            fontSize = 18.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.location),
                                contentDescription = "Icon Location",
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = like.user.address,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 15.dp),
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 15.dp, vertical = 10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(MaterialTheme.colors.background)
                            .border(1.dp, Color.Red, RoundedCornerShape(5.dp))
                            .clickable(onClick = { onUnlikeShop(like) })
                    ) {
                        Text(
                            text = "Unlike",
                            color = Color.Red,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
                Divider(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}