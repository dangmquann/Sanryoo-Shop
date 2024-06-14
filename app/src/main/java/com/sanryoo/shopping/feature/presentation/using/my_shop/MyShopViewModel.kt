package com.sanryoo.shopping.feature.presentation.using.my_shop

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.sanryoo.shopping.feature.domain.model.Product
import com.sanryoo.shopping.feature.domain.model.User
import com.sanryoo.shopping.feature.presentation._base_component.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MyShopViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : BaseViewModel<MyShopState, MyShopUiEvent>(MyShopState()) {

    private val usersCollectionRef = Firebase.firestore.collection("users")
    private val productsCollectionRef = Firebase.firestore.collection("products")

    private var userListener: ListenerRegistration? = null
    private var productsListener: ListenerRegistration? = null

    init {
        getShopInformation()
    }

    private fun getShopInformation() {
        viewModelScope.launch {
            auth.currentUser?.run {

                userListener = usersCollectionRef
                    .document(uid)
                    .addSnapshotListener { documentSnapshot, error ->
                        error?.let { e ->
                            e.printStackTrace()
                            return@addSnapshotListener
                        }
                        documentSnapshot?.let { snapShot ->
                            snapShot.toObject<User>()?.let { user ->
                                _state.update { it.copy(user = user) }
                            }
                        }
                    }

                productsListener = productsCollectionRef
                    .whereEqualTo("user.uid", uid)
                    .addSnapshotListener { querySnapshot, error ->
                        error?.let {
                            return@addSnapshotListener
                        }
                        querySnapshot?.let { snapshot ->
                            val products = snapshot.documents.mapNotNull { it.toObject<Product>() }
                            _state.update { it.copy(products = products) }
                        }
                    }
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            try {
                val productDocumentRef = productsCollectionRef.document(product.pid)
                productDocumentRef.delete().await()
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        userListener?.remove()
        productsListener?.remove()
    }
}