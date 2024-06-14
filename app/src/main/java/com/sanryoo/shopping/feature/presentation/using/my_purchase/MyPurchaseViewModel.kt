package com.sanryoo.shopping.feature.presentation.using.my_purchase

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.sanryoo.shopping.feature.domain.model.Order
import com.sanryoo.shopping.feature.presentation._base_component.BaseViewModel
import com.sanryoo.shopping.feature.util.OrderStatus.CANCELLED
import com.sanryoo.shopping.feature.util.OrderStatus.ORDERED
import com.sanryoo.shopping.feature.util.OrderStatus.SHIPPED
import com.sanryoo.shopping.feature.util.OrderStatus.SHIPPING
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MyPurchaseViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : BaseViewModel<MyPurchaseState, MyPurchaseUiEvent>(MyPurchaseState()) {

    private val ordersCollectionRef = Firebase.firestore.collection("orders")

    private var purchaseListener: ListenerRegistration? = null

    init {
        getPurchase()
    }

    private fun getPurchase() {
        auth.currentUser?.run {
            purchaseListener = ordersCollectionRef
                .whereNotEqualTo("orderedDate", null)
                .orderBy("orderedDate", Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapShot, error ->
                    error?.let { e ->
                        e.printStackTrace()
                        return@addSnapshotListener
                    }
                    querySnapShot?.let { snapShot ->
                        val orders = snapShot.documents
                            .mapNotNull { it.toObject<Order>() }
                            .filter { it.customer.uid == uid }
                        _state.update { state ->
                            state.copy(
                                allOrders = orders,
                                orderedOrders = orders.filter { it.status == ORDERED },
                                shippingOrders = orders.filter { it.status == SHIPPING },
                                shippedOrders = orders.filter { it.status == SHIPPED },
                                cancelledOrders = orders.filter { it.status == CANCELLED }
                            )
                        }
                    }
                }
        }
    }

    fun setNewTab(newTab: Int) {
        _state.update { it.copy(oldTab = it.currentTab, currentTab = newTab) }
    }

    fun cancelOrder(order: Order) {
        viewModelScope.launch {
            try {
                val orderDocumentRef = ordersCollectionRef.document(order.oid)
                orderDocumentRef.set(
                    order.copy(
                        status = CANCELLED,
                        cancelledDate = Date(System.currentTimeMillis())
                    )
                ).await()
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        purchaseListener?.remove()
    }

}