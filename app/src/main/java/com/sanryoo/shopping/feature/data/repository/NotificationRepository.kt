package com.sanryoo.shopping.feature.data.repository

import com.sanryoo.shopping.feature.domain.model.notification.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response

interface NotificationRepository {

    suspend fun postNotification(notification: PushNotification) : Response<ResponseBody>

}