package com.sanryoo.shopping.feature.domain.model.notification

data class PushNotification(
    val data: Notification,
    val to: String
)
