package com.sanryoo.shopping.feature.presentation.using.product

import android.app.Application
import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sanryoo.shopping.feature.domain.model.Order
import com.sanryoo.shopping.feature.domain.model.Product
import com.sanryoo.shopping.feature.domain.model.Review
import com.sanryoo.shopping.feature.domain.model.User
import com.sanryoo.shopping.feature.presentation._base_component.BaseViewModel
import com.sanryoo.shopping.feature.util.OrderStatus.ADDED_TO_CART
import com.sanryoo.shopping.feature.util.getFileExtension
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : BaseViewModel<ProductState, ProductUiEvent>(ProductState()) {

    private val usersCollectionRef = Firebase.firestore.collection("users")
    private val productsCollectionRef = Firebase.firestore.collection("products")
    private val ordersCollectionRef = Firebase.firestore.collection("orders")

    private var numberOfCartListener: ListenerRegistration? = null

    init {
        getUser()
        getNumberOfCart()
    }

    private fun getUser() {
        viewModelScope.launch {
            auth.currentUser?.run {
                usersCollectionRef
                    .document(uid)
                    .addSnapshotListener { documentSnapShot, error ->
                        error?.let { e ->
                            e.printStackTrace()
                            return@addSnapshotListener
                        }
                        documentSnapShot?.let { snapShot ->
                            val getUser = snapShot.toObject<User>()
                            getUser?.let { user ->
                                _state.update { it.copy(user = user) }
                            }
                        }
                    }
            }
        }
    }

    fun getInformationProduct(product: Product) {

        // Observe product
        val productDocument = productsCollectionRef.document(product.pid)
        productDocument.addSnapshotListener { documentSnapshot, error ->
            error?.let { e ->
                e.printStackTrace()
                return@addSnapshotListener
            }
            documentSnapshot?.let { snapshot ->
                snapshot.toObject<Product>()?.let { product1 ->
                    _state.update {
                        it.copy(
                            product = product1,
                            addToCart = state.value.addToCart.copy(product = product1)
                        )
                    }
                }
            }
        }

        //Observe product of shop
        productsCollectionRef
            .whereEqualTo("user.uid", product.user.uid)
            .addSnapshotListener { querySnapshot, error ->
                error?.let { e ->
                    e.printStackTrace()
                    return@addSnapshotListener
                }
                querySnapshot?.let { snapshot ->
                    val productsOfShop = snapshot.documents
                        .mapNotNull { it.toObject<Product>() }
                        .filter { it.pid != state.value.product.pid }
                    _state.update { it.copy(productOfShop = productsOfShop) }
                }
            }

        //Observe similar product
        productsCollectionRef
            .whereArrayContainsAny("category", product.category)
            .orderBy("sold", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, error ->
                error?.let { e ->
                    e.printStackTrace()
                    return@addSnapshotListener
                }
                querySnapshot?.let { snapshot ->
                    val similarProduct = snapshot.documents
                        .mapNotNull { it.toObject<Product>() }
                        .filter { it.pid != state.value.product.pid }
                    _state.update { it.copy(similarProducts = similarProduct) }
                }
            }

    }

    private fun getNumberOfCart() {
        auth.addAuthStateListener {
            numberOfCartListener?.remove()

            if (auth.currentUser == null) {
                _state.update { it.copy(numberOfCart = 0) }
            }

            auth.currentUser?.run {
                numberOfCartListener = ordersCollectionRef
                    .where(
                        Filter.and(
                            Filter.equalTo("customer.uid", uid),
                            Filter.equalTo("status", ADDED_TO_CART)
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
    }

    fun setFirstItemOffset(offset: Offset) {
        _state.update { it.copy(firstItemOffset = offset) }
    }

    fun setSheetContent(sheetContent: SheetContent) {
        _state.update { it.copy(sheetContent = sheetContent) }
    }

    fun onClickCart() {
        if (auth.currentUser != null) {
            onUiEvent(ProductUiEvent.NavigateToCart)
        } else {
            onUiEvent(ProductUiEvent.NavigateToLogIn)
        }
    }

    fun onClickChats() {
        if (auth.currentUser != null) {
            onUiEvent(ProductUiEvent.NavigateToChats)
        } else {
            onUiEvent(ProductUiEvent.NavigateToLogIn)
        }
    }

    fun onClickAddToCart() {
        if (auth.currentUser != null) {
            setSheetContent(SheetContent.ADD_TO_CART)
            onUiEvent(ProductUiEvent.SetShowBottomSheet(true))
        } else {
            onUiEvent(ProductUiEvent.NavigateToLogIn)
        }
    }

    fun onClickBuyNow() {
        if (auth.currentUser != null) {
            setSheetContent(SheetContent.BUY_NOW)
            onUiEvent(ProductUiEvent.SetShowBottomSheet(true))
        } else {
            onUiEvent(ProductUiEvent.NavigateToLogIn)
        }
    }

    fun onAddToCartChange(order: Order) {
        _state.update { it.copy(addToCart = order) }
    }

    fun onAddToCart() {
        viewModelScope.launch {
            try {
                val orderDocumentRef = ordersCollectionRef.document()
                val order = state.value.addToCart.copy(
                    oid = orderDocumentRef.id,
                    customer = state.value.user,
                    product = state.value.product,
                    status = ADDED_TO_CART
                )
                orderDocumentRef.set(order).await()
                onUiEvent(ProductUiEvent.SetShowBottomSheet(false))
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }
        }
    }
}