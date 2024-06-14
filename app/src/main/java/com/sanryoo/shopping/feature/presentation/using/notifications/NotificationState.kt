package com.sanryoo.shopping.feature.presentation.using.notifications

import com.sanryoo.shopping.feature.domain.model.notification.Notification

data class NotificationState(
    var notifications: List<Notification> = emptyList()
)
