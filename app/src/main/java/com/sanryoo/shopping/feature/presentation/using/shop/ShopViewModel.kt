package com.sanryoo.shopping.feature.presentation.using.shop

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.sanryoo.shopping.feature.domain.model.Like
import com.sanryoo.shopping.feature.domain.model.Product
import com.sanryoo.shopping.feature.domain.model.User
import com.sanryoo.shopping.feature.presentation._base_component.BaseViewModel
import com.sanryoo.shopping.feature.util.OrderStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : BaseViewModel<ShopState, ShopUiEvent>(ShopState()) {

    private val usersCollectionRef = Firebase.firestore.collection("users")
    private val productsCollectionRef = Firebase.firestore.collection("products")
    private val likesCollectionRef = Firebase.firestore.collection("likes")
    private val ordersCollectionRef = Firebase.firestore.collection("orders")

    private var numberOfCartListener: ListenerRegistration? = null

    init {
        auth.addAuthStateListener {
            getUser()
            getNumberOfCart()
        }
    }

    private fun getUser() {
        auth.currentUser?.run {
            usersCollectionRef
                .document(uid)
                .addSnapshotListener { documentSnapshot, error ->
                    error?.let { e ->
                        e.printStackTrace()
                        return@addSnapshotListener
                    }
                    documentSnapshot?.let { snapshot ->
                        snapshot.toObject<User>()?.let { user ->
                            _state.update { it.copy(user = user) }

                        }
                    }
                }
        }
    }

    fun onClickCart() {
        if (auth.currentUser != null) {
            onUiEvent(ShopUiEvent.NavigateToCart)
        } else {
            onUiEvent(ShopUiEvent.NavigateToLogIn)
        }
    }

    fun onClickChats() {
        if (auth.currentUser != null) {
            onUiEvent(ShopUiEvent.NavigateToChats)
        } else {
            onUiEvent(ShopUiEvent.NavigateToLogIn)
        }
    }

    fun getShop(shopId: String) {

        //Get Shop Information
        usersCollectionRef
            .document(shopId)
            .addSnapshotListener { documentSnapshot, error ->
                error?.let { e ->
                    e.printStackTrace()
                    return@addSnapshotListener
                }
                documentSnapshot?.let { snapshot ->
                    snapshot.toObject<User>()?.let { user ->
                        _state.update { it.copy(shop = user) }

                    }
                }
            }

        //Get Shop's Product
        productsCollectionRef
            .whereEqualTo("user.uid", shopId)
            .addSnapshotListener { querySnapshot, error ->
                error?.let {
                    return@addSnapshotListener
                }
                querySnapshot?.let { snapshot ->
                    val products = snapshot.documents.mapNotNull { it.toObject<Product>() }
                    _state.update { it.copy(products = products) }
                }
            }

        //Number of likes
        likesCollectionRef
            .whereEqualTo("user.uid", shopId)
            .addSnapshotListener { querySnapshot, error ->
                error?.let { e ->
                    e.printStackTrace()
                    return@addSnapshotListener
                }
                querySnapshot?.let { snapShot ->
                    _state.update { it.copy(numberOfLikes = snapShot.size().toLong()) }
                }
            }

        //Liked
        auth.currentUser?.run {
            likesCollectionRef.where(
                Filter.and(
                    Filter.equalTo("liker.uid", uid),
                    Filter.equalTo("user.uid", shopId),
                )
            ).addSnapshotListener { querySnapshot, error ->
                error?.let { e ->
                    e.printStackTrace()
                    return@addSnapshotListener
                }
                querySnapshot?.let { snapshot ->
                    _state.update { it.copy(liked = !snapshot.isEmpty) }
                }
            }
        }
    }

    private fun getNumberOfCart() {
        numberOfCartListener?.remove()

        if (auth.currentUser == null) {
            _state.update { it.copy(numberOfCart = 0) }
            return
        }

        auth.currentUser?.run {
            numberOfCartListener = ordersCollectionRef
                .where(
                    Filter.and(
                        Filter.equalTo("customer.uid", uid),
                        Filter.equalTo("status", OrderStatus.ADDED_TO_CART)
                    )
                )
                .addSnapshotListener { querySnapShot, error ->
                    error?.let { e ->
                        e.printStackTrace()
                        return@addSnapshotListener
                    }
                    querySnapShot?.let { snapShot ->
                        _state.update { it.copy(numberOfCart = snapShot.documents.size) }
                    }
                }
        }
    }

    fun likeShop() {
        if (auth.currentUser == null) {
            onUiEvent(ShopUiEvent.NavigateToLogIn)
            return
        }
        viewModelScope.launch {
            try {
                val likeDocumentRef = likesCollectionRef.document()
                val like = Like(
                    lid = likeDocumentRef.id,
                    liker = state.value.user,
                    user = state.value.shop
                )
                likeDocumentRef.set(like).await()
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }
        }
    }

    fun unlikeShop() {
        viewModelScope.launch {
            try {
                val likeQuerySnapshot = likesCollectionRef
                    .where(
                        Filter.and(
                            Filter.equalTo("liker.uid", state.value.user.uid),
                            Filter.equalTo("user.uid", state.value.shop.uid),
                        )
                    )
                    .get()
                    .await()

                likeQuerySnapshot.documents.forEach { documentSnapShot ->
                    likesCollectionRef.document(documentSnapShot.id).delete().await()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }
        }
    }

}