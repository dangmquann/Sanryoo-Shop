package com.sanryoo.shopping.feature.data.repository

import com.sanryoo.shopping.feature.data.api.NotificationAPI
import com.sanryoo.shopping.feature.domain.model.notification.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response

class NotificationRepositoryImpl(
    private val notificationAPI: NotificationAPI
) : NotificationRepository {

    override suspend fun postNotification(notification: PushNotification): Response<ResponseBody> {
        return notificationAPI.postNotification(notification)
    }

}