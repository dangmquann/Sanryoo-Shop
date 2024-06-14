package com.sanryoo.shopping.feature.presentation.using.my_likes

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.sanryoo.shopping.feature.domain.model.Like
import com.sanryoo.shopping.feature.presentation._base_component.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MyLikesViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : BaseViewModel<MyLikesState, MyLikesUiEvent>(MyLikesState()) {

    private val likesCollectionRef = Firebase.firestore.collection("likes")
    private var likedShopsListener: ListenerRegistration? = null

    init {
        getLikedShops()
    }

    private fun getLikedShops() {
        _state.update { it.copy(likes = emptyList()) }
        auth.currentUser?.run {
            likedShopsListener = likesCollectionRef
                .whereEqualTo("liker.uid", uid)
                .addSnapshotListener { querySnapshot, error ->
                    error?.let { e ->
                        e.printStackTrace()
                        return@addSnapshotListener
                    }
                    querySnapshot?.let { snapshot ->
                        val likes = snapshot.documents.mapNotNull { it.toObject<Like>() }
                        _state.update { it.copy(likes = likes) }
                    }
                }
        }
    }

    fun unlikeShop(like: Like) {
        viewModelScope.launch {
            try {
                val likeRef = likesCollectionRef.document(like.lid)
                likeRef.delete().await()
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        likedShopsListener?.remove()
    }

}