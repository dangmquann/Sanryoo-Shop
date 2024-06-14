package com.sanryoo.shopping.di

import com.sanryoo.shopping.feature.data.api.NotificationAPI
import com.sanryoo.shopping.feature.data.repository.NotificationRepository
import com.sanryoo.shopping.feature.data.repository.NotificationRepositoryImpl
import com.sanryoo.shopping.feature.util.FirebaseMessageConstant
import com.sanryoo.shopping.feature.util.FirebaseMessageConstant.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
object APIModule {

    @Provides
    @ViewModelScoped
    fun provideNotificationAPI(): NotificationAPI = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NotificationAPI::class.java)

    @Provides
    @ViewModelScoped
    fun provideRepository(notificationAPI: NotificationAPI) : NotificationRepository {
        return NotificationRepositoryImpl(notificationAPI)
    }

}