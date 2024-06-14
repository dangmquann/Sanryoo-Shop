package com.sanryoo.shopping.feature.presentation.using.profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sanryoo.shopping.R
import com.sanryoo.shopping.feature.presentation._component.shimmerEffect
import com.sanryoo.shopping.feature.presentation.using.profile.component.ItemProfile
import com.sanryoo.shopping.feature.util.Screen
import com.sanryoo.shopping.ui.theme.Primary
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is ProfileUiEvent.NavigateToCart -> {
                    navController.navigate(Screen.Cart.route)
                }

                is ProfileUiEvent.NavigateToChats -> {
                    navController.navigate(Screen.Chats.route)
                }

                is ProfileUiEvent.NavigateToLogIn -> {
                    navController.navigate(Screen.LogIn.route)
                }

                is ProfileUiEvent.NavigateToSignUp -> {
                    navController.navigate(Screen.SignUp.route)
                }

                is ProfileUiEvent.NavigateToUserScreen -> {
                    navController.navigate(Screen.User.route)
                }

                is ProfileUiEvent.NavigateToMyPurchase -> {
                    navController.navigate(Screen.MyPurchase.route)
                }

                is ProfileUiEvent.NavigateToMyLikesScreen -> {
                    navController.navigate(Screen.MyLikes.route)
                }

                is ProfileUiEvent.NavigateToPasswordScreen -> {
                    navController.navigate(Screen.ChangePassword.route)
                }

                is ProfileUiEvent.NavigateToShopScreen -> {
                    navController.navigate(Screen.MyShop.route)
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    ProfileContent(
        state = state,
        onClickCart = viewModel::onClickCart,
        onClickChats = viewModel::onClickChats,
        onClickMyAccount = viewModel::onClickMyAccount,
        onClickMyShop = viewModel::onClickMyShop,
        onClickMyPurchase = viewModel::onClickMyPurchase,
        onClickMyLikes = viewModel::onClickMyLikes,
        onSignOut = viewModel::signOut,
        onClickChangePassword = { viewModel.onUiEvent(ProfileUiEvent.NavigateToPasswordScreen) },
        onLogIn = { viewModel.onUiEvent(ProfileUiEvent.NavigateToLogIn) },
        onSignUp = { viewModel.onUiEvent(ProfileUiEvent.NavigateToSignUp) },
    )
}

@Composable
private fun ProfileContent(
    state: ProfileState = ProfileState(),
    onClickCart: () -> Unit = {},
    onClickChats: () -> Unit = {},
    onClickMyAccount: () -> Unit = {},
    onClickMyShop: () -> Unit = {},
    onClickMyPurchase: () -> Unit = {},
    onClickMyLikes: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onClickChangePassword: () -> Unit = {},
    onLogIn: () -> Unit = {},
    onSignUp: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .statusBarsPadding()
                            .fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
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
                                tint = Color.White
                            )
                            if (state.numberOfCart > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = state.numberOfCart.toString(),
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
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
                                tint = Color.White
                            )
                            if (state.numberOfChats > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = state.numberOfChats.toString(),
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = state.photoUrl.ifBlank { R.drawable.user },
                            contentDescription = "Profile picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .size(80.dp)
                                .clip(CircleShape)
                                .shimmerEffect()
                        )
                        if (state.isLogged) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = state.displayName,
                                    maxLines = 1,
                                    fontSize = 20.sp,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 5.dp)
                                )
                                Text(
                                    text = state.email,
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 5.dp)
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                            OutlinedButton(
                                onClick = onLogIn,
                                modifier = Modifier.padding(horizontal = 10.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color.White,
                                    contentColor = Primary
                                )
                            ) {
                                Text(text = "Log In")
                            }
                            OutlinedButton(
                                onClick = onSignUp,
                                modifier = Modifier.padding(horizontal = 10.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color.White,
                                    contentColor = Primary
                                )
                            ) {
                                Text(text = "Sign up")
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            ItemProfile(
                icon = R.drawable.user,
                label = "My Account",
                onClick = onClickMyAccount
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            )
            if (state.isLogged && state.isEmailAuth) {
                ItemProfile(
                    icon = R.drawable.lock,
                    label = "Change password",
                    onClick = onClickChangePassword
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp)
                )
            }
            ItemProfile(
                icon = R.drawable.store,
                label = "My Shop",
                onClick = onClickMyShop
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            )
            ItemProfile(
                icon = R.drawable.list_purchase,
                label = "My Purchases",
                onClick = onClickMyPurchase
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            )
            ItemProfile(
                icon = R.drawable.love,
                label = "My Likes",
                onClick = onClickMyLikes
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            )
            ItemProfile(
                icon = R.drawable.help,
                label = "Help Centre"
            )
            if (state.isLogged) {
                Button(
                    onClick = onSignOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Log out")
                }
            }
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}