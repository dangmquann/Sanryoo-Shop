package com.sanryoo.shopping.feature.presentation._component

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sanryoo.shopping.R
import com.sanryoo.shopping.feature.util.Screen
import com.sanryoo.shopping.ui.theme.Inactive
import com.sanryoo.shopping.ui.theme.Primary

@Composable
fun CustomBottomNavigation(
    navController: NavHostController,
    numberOfNotifications: Int
) {
    val backStackEntry = navController.currentBackStackEntryAsState().value
    val destination = backStackEntry?.destination?.route

    val items = listOf(BottomNavItem.Home, BottomNavItem.Notify, BottomNavItem.Profile)
    val showBottomBar = items.any { item ->
        item.route == destination
    }

    if (showBottomBar) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background),
        ) {
            Row(
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .height(50.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = item.route == destination
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick = {
                                    if (destination != item.route) {
                                        navController.navigate(item.route) {
                                            popUpTo(Screen.Home.route)
                                            launchSingleTop = true
                                        }
                                    }
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box {
                            Icon(
                                painter = painterResource(id = if (isSelected) item.selected else item.unselected),
                                contentDescription = "Bottom item icon",
                                modifier = Modifier
                                    .padding(
                                        top = if(item == BottomNavItem.Notify) 5.dp else 0.dp,
                                        bottom = if(item == BottomNavItem.Notify) 5.dp else 0.dp,
                                        end = if(item == BottomNavItem.Notify) 5.dp else 0.dp,
                                    )
                                    .size(35.dp),
                                tint = if (isSelected) Primary else Inactive
                            )
                            if(item == BottomNavItem.Notify && numberOfNotifications > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = numberOfNotifications.toString(),
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                        AnimatedVisibility(
                            visible = isSelected,
                            enter = fadeIn() + expandHorizontally(),
                            exit = fadeOut() + shrinkHorizontally()
                        ) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(modifier = Modifier.background(Primary, RoundedCornerShape(100))) {
                                Text(
                                    text = item.label,
                                    color = MaterialTheme.colors.background,
                                    modifier = Modifier.padding(horizontal = 5.dp),
                                    maxLines = 1,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val selected: Int,
    val unselected: Int
) {
    object Home : BottomNavItem(
        Screen.Home.route,
        "Home",
        R.drawable.home_selected,
        R.drawable.home_unselected
    )

    object Notify : BottomNavItem(
        Screen.Notification.route,
        "Notifications",
        R.drawable.notify_selected,
        R.drawable.notify_unselected
    )

    object Profile : BottomNavItem(
        Screen.Profile.route,
        "Profile",
        R.drawable.profile_selected,
        R.drawable.profile_unselected
    )
}