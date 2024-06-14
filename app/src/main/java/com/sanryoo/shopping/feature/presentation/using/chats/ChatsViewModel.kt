package com.sanryoo.shopping.feature.presentation.using.chats

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.sanryoo.shopping.feature.domain.model.Message
import com.sanryoo.shopping.feature.domain.model.User
import com.sanryoo.shopping.feature.presentation._base_component.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : BaseViewModel<ChatsState, ChatsUIEvent>(ChatsState()) {

    private val usersCollection = Firebase.firestore.collection("users")
    private val messagesCollection = Firebase.firestore.collection("messages")

    init {
        getUser()
        getAllMessages()
    }

    private fun getUser() {
        auth.currentUser?.run {
            usersCollection.document(uid)
                .addSnapshotListener { documentSnapShot, error ->
                    error?.let { e ->
                        e.printStackTrace()
                        return@addSnapshotListener
                    }
                    documentSnapShot?.let { snapShot ->
                        snapShot.toObject<User>()?.let { user ->
                            _state.update { it.copy(user = user) }
                        }
                    }
                }
        }
    }

    private fun getAllMessages() {
        auth.currentUser?.run {
            messagesCollection.where(
                Filter.or(
                    Filter.equalTo("from.uid", uid),
                    Filter.equalTo("to.uid", uid)
                )
            ).addSnapshotListener { querySnapShot, error ->
                error?.let { e ->
                    e.printStackTrace()
                    return@addSnapshotListener
                }
                querySnapShot?.let { snapShot ->
                    val messages = snapShot.documents
                        .mapNotNull { it.toObject<Message>() }
                        .groupBy { it.groupId }
                        .values
                        .mapNotNull { messages ->
                            messages.maxByOrNull { it.date!! }
                        }
                        .sortedByDescending { it.date }

                    _state.update { it.copy(messages = messages) }
                }
            }
        }
    }

}