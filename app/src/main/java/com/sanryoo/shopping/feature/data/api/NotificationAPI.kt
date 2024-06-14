package com.sanryoo.shopping.feature.data.api

import com.google.firebase.messaging.FirebaseMessaging
import com.sanryoo.shopping.feature.domain.model.notification.PushNotification
import com.sanryoo.shopping.feature.util.FirebaseMessageConstant
import com.sanryoo.shopping.feature.util.FirebaseMessageConstant.CONTENT_TYPE
import com.sanryoo.shopping.feature.util.FirebaseMessageConstant.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type: $CONTENT_TYPE")
    @POST("/fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>

}