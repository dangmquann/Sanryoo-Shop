package com.sanryoo.shopping.feature.presentation.using.checkout

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.sanryoo.shopping.feature.data.repository.NotificationRepository
import com.sanryoo.shopping.feature.domain.model.Order
import com.sanryoo.shopping.feature.domain.model.User
import com.sanryoo.shopping.feature.domain.model.fcmtoken.FCMToken
import com.sanryoo.shopping.feature.domain.model.notification.Notification
import com.sanryoo.shopping.feature.domain.model.notification.PushNotification
import com.sanryoo.shopping.feature.presentation._base_component.BaseViewModel
import com.sanryoo.shopping.feature.util.OrderStatus.ORDERED
import com.sanryoo.shopping.feature.util.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CheckOutViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val notificationRepository: NotificationRepository
) : BaseViewModel<CheckOutState, CheckOutUiEvent>(CheckOutState()) {

    private val usersCollectionRef = Firebase.firestore.collection("users")
    private val ordersCollectionRef = Firebase.firestore.collection("orders")
    private val tokensCollectionRef = Firebase.firestore.collection("tokens")
    private val notificationsCollectionRef = Firebase.firestore.collection("notifications")

    init {
        getUserInformation()
    }

    private fun getUserInformation() {
        viewModelScope.launch {
            auth.currentUser?.run {
                val userDocumentRef = usersCollectionRef.document(uid)
                val documentSnapShot = userDocumentRef.get().await()
                documentSnapShot?.let { snapShot ->
                    snapShot.toObject<User>()?.let { user ->
                        _state.update { it.copy(user = user) }
                    }
                }
            }
        }
    }

    fun onOrdersChange(orders: List<Order>) {
        _state.update { it.copy(orders = orders) }
    }

    fun placeOrder() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(loading = true) }
                state.value.orders.forEach { order ->
                    val orderDocumentRef = if (order.oid.isNotBlank()) {
                        ordersCollectionRef.document(order.oid)
                    } else {
                        ordersCollectionRef.document()
                    }
                    orderDocumentRef.set(
                        order.copy(
                            oid = orderDocumentRef.id,
                            customer = state.value.user,
                            status = ORDERED,
                            orderedDate = Date(System.currentTimeMillis()),
                            shippedDate = null,
                            cancelledDate = null
                        )
                    ).await()

                    val tokenDocumentRef = tokensCollectionRef.document(order.product.user.uid)
                    val tokenDocumentSnapShot = tokenDocumentRef.get().await()

                    val notificationDocumentRef = notificationsCollectionRef.document()
                    val notification = Notification(
                        nid = notificationDocumentRef.id,
                        to = order.product.user.uid,
                        image = order.product.images[0],
                        title = "Order product ${order.product.name}",
                        message = "${state.value.user.name} just placed an order for you, please confirm",
                        date = Date(System.currentTimeMillis()),
                        seen = false,
                        read = false,
                        route = Screen.MyShopPurchases.route
                    )
                    notificationDocumentRef.set(notification).await()

                    tokenDocumentSnapShot.toObject<FCMToken>()?.run {
                        val postNotification = PushNotification(
                            data = Notification(
                                title = notification.title,
                                message = notification.message,
                                route = Screen.Notification.route
                            ),
                            to = token
                        )
                        notificationRepository.postNotification(postNotification)
                    }
                }
                _state.update { it.copy(loading = false) }
                onUiEvent(CheckOutUiEvent.BackToPrevScreen)
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { it.copy(loading = false) }
                onUiEvent(CheckOutUiEvent.ShowSnackBar("Error! Can not place order"))
            }
        }
    }

}