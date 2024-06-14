package com.sanryoo.shopping.feature.presentation.using.notifications

sealed class NotificationsUIEvent {
    data class NavigateToRoute(val route: String) : NotificationsUIEvent()
}
