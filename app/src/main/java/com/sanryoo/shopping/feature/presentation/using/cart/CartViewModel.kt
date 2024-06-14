package com.sanryoo.shopping.feature.presentation.using.cart

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.sanryoo.shopping.feature.domain.model.Order
import com.sanryoo.shopping.feature.presentation._base_component.BaseViewModel
import com.sanryoo.shopping.feature.util.OrderStatus.ADDED_TO_CART
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : BaseViewModel<CartState, CartUiEvent>(CartState()) {

    private val ordersCollectionRef = Firebase.firestore.collection("orders")

    private var cartListener: ListenerRegistration? = null

    init {
        getCart()
    }

    private fun getCart() {
        auth.currentUser?.run {
            cartListener = ordersCollectionRef.where(
                Filter.and(
                    Filter.equalTo("customer.uid", uid),
                    Filter.equalTo("status", ADDED_TO_CART)
                )
            ).addSnapshotListener { querySnapShot, error ->
                error?.let { e ->
                    e.printStackTrace()
                    return@addSnapshotListener
                }
                querySnapShot?.let { snapShot ->
                    val orders = snapShot.documents
                        .mapNotNull { it.toObject<Order>() }
                        .map { CartOrder(order = it, checked = false) }
                    _state.update { it.copy(orders = orders) }
                }
            }
        }
    }

    fun onCartChange(orders: List<CartOrder>) {
        _state.update { it.copy(orders = orders) }
    }

    fun onEditOrderChange(order: Order) {
        _state.update { it.copy(editOrder = order) }
    }

    fun editOrder() {
        viewModelScope.launch {
            try {
                val documentRef = ordersCollectionRef.document(state.value.editOrder.oid)
                documentRef.set(state.value.editOrder).await()
                onUiEvent(CartUiEvent.SetShowBottomSheet(false))
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }
        }
    }

    fun deleteOrderInCart(order: Order) {
        viewModelScope.launch {
            try {
                val documentRef = ordersCollectionRef.document(order.oid)
                documentRef.delete().await()
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        cartListener?.remove()
    }

}