package com.sanryoo.shopping.feature.presentation.using.review

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sanryoo.shopping.feature.domain.model.Order
import com.sanryoo.shopping.feature.domain.model.Product
import com.sanryoo.shopping.feature.domain.model.Review
import com.sanryoo.shopping.feature.presentation._base_component.BaseViewModel
import com.sanryoo.shopping.feature.util.getFileExtension
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val appContext: Application
) : BaseViewModel<ReviewState, ReviewUIEvent>(ReviewState()) {

    private val productsCollectionRef = Firebase.firestore.collection("products")
    private val ordersCollectionRef = Firebase.firestore.collection("orders")

    private val productsStorageRef = Firebase.storage.reference.child("products")

    fun setOrder(order: Order) {
        _state.update { it.copy(order = order) }
    }

    fun onRateChange(newRate: Int) {
        _state.update { it.copy(rate = newRate) }
    }

    fun onCommentChange(newValue: String) {
        _state.update { it.copy(comment = newValue) }
    }

    fun onListImageCommentChange(images: List<Uri>) {
        _state.update { it.copy(listImagesComment = images) }
    }

    fun sendReview() {
        onUiEvent(ReviewUIEvent.ClearFocus)
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                var tempListImage = emptyList<String>()
                val productsRef = productsStorageRef.child("/${state.value.order.product.pid}/images-review")
                state.value.listImagesComment.forEach { uri ->
                    val fileExtension = getFileExtension(uri, appContext.contentResolver)
                    val randomFileName =
                        "${UUID.randomUUID()}-${System.currentTimeMillis()}.$fileExtension"
                    val productImageRef = productsRef.child(randomFileName)
                    val taskSnapshot = productImageRef.putFile(uri).await()

                    val task = taskSnapshot.task
                    if (task.isComplete) {
                        val url = productImageRef.downloadUrl.await()
                        val path = url?.toString() ?: ""
                        tempListImage = tempListImage + path
                    }
                }
                val review = Review(
                    user = state.value.order.customer,
                    time = Date(System.currentTimeMillis()),
                    rate = state.value.rate,
                    comment = state.value.comment,
                    images = tempListImage
                )
                val isSuccessful = Firebase.firestore.runTransaction<Boolean> { transaction ->

                    val productDocument = productsCollectionRef.document(state.value.order.product.pid)
                    val orderDocument = ordersCollectionRef.document(state.value.order.oid)

                    val snapShot = transaction.get(productDocument)
                    snapShot.toObject<Product>()?.run {
                        transaction.update(productDocument, "reviews", reviews + review)
                        transaction.update(orderDocument, "reviewed", true)
                        return@runTransaction true
                    }
                    return@runTransaction false
                }.await()

                if (isSuccessful) {
                    _state.update { it.copy(isLoading = false) }
                    onUiEvent(ReviewUIEvent.BackToPrevScreen)
                } else {
                    _state.update { it.copy(isLoading = false) }
                    onUiEvent(ReviewUIEvent.ShowSnackBar("Error! Can not send review"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { it.copy(isLoading = false) }
                onUiEvent(ReviewUIEvent.ShowSnackBar(e.message ?: "Error! Can not send review"))
            }
        }
    }

}