package com.sanryoo.shopping.feature.presentation.using.message

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sanryoo.shopping.feature.data.repository.NotificationRepository
import com.sanryoo.shopping.feature.domain.model.Message
import com.sanryoo.shopping.feature.domain.model.Order
import com.sanryoo.shopping.feature.domain.model.User
import com.sanryoo.shopping.feature.domain.model.fcmtoken.FCMToken
import com.sanryoo.shopping.feature.domain.model.notification.Notification
import com.sanryoo.shopping.feature.domain.model.notification.PushNotification
import com.sanryoo.shopping.feature.presentation._base_component.BaseViewModel
import com.sanryoo.shopping.feature.util.Screen
import com.sanryoo.shopping.feature.util.getFileExtension
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val notificationRepository: NotificationRepository,
    private val appContext: Application
) : BaseViewModel<MessageState, MessageUIEvent>(MessageState()) {

    private val usersCollection = Firebase.firestore.collection("users")
    private val messagesCollection = Firebase.firestore.collection("messages")
    private val tokensCollection = Firebase.firestore.collection("tokens")

    private val messagesStorageRef = Firebase.storage.reference.child("messages")

    init {
        getUser()
    }

    private fun getUser() {
        auth.currentUser?.run {
            usersCollection.document(uid).addSnapshotListener { documentSnapShot, error ->
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

    fun getOthers(othersId: String) {
        usersCollection.document(othersId).addSnapshotListener { documentSnapShot, error ->
            error?.let { e ->
                e.printStackTrace()
                return@addSnapshotListener
            }
            documentSnapShot?.let { snapShot ->
                snapShot.toObject<User>()?.let { user ->
                    _state.update { it.copy(others = user) }
                }
            }
        }
    }

    fun getMessages(othersId: String) {
        auth.currentUser?.run {
            messagesCollection
                .where(
                    Filter.or(
                        Filter.and(
                            Filter.equalTo("from.uid", uid),
                            Filter.equalTo("to.uid", othersId)
                        ),
                        Filter.and(
                            Filter.equalTo("from.uid", othersId),
                            Filter.equalTo("to.uid", uid)
                        )
                    )
                )
                .addSnapshotListener { querySnapShot, error ->
                    error?.let { e ->
                        e.printStackTrace()
                        return@addSnapshotListener
                    }
                    querySnapShot?.let { snapShot ->
                        val messages = snapShot.documents
                            .mapNotNull { it.toObject<Message>() }
                            .sortedByDescending { it.date }
                        _state.update { it.copy(messages = messages) }

                        //Mark message to user as read
                        messages.filter {
                            it.to.uid == uid && !it.read
                        }.onEach {
                            maskMessageAsRead(it.mid)
                        }
                    }
                }
        }
    }

    private fun maskMessageAsRead(mid: String) {
        viewModelScope.launch {
            try {
                messagesCollection.document(mid).update("read", true).await()
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }
        }
    }


    fun onTextChange(newValue: String) {
        _state.update { it.copy(text = newValue) }
    }

    fun onImagesChange(images: List<Uri>) {
        _state.update { it.copy(images = images) }
    }

    fun onSendMessage() {
        viewModelScope.launch {
            val tempState = state.value
            _state.update {
                it.copy(
                    text = "",
                    images = emptyList(),
                    sending = true
                )
            }
            onUiEvent(MessageUIEvent.ScrollToFirstItem)
            try {
                val messageDocument = messagesCollection.document()

                val groupId = if (tempState.user.uid < tempState.others.uid) {
                    "${tempState.user.uid}_${tempState.others.uid}"
                } else {
                    "${tempState.others.uid}_${tempState.user.uid}"
                }

                val messageRef = messagesStorageRef.child(groupId)
                var images = emptyList<String>()

                tempState.images.forEach { uri ->
                    val fileExt = getFileExtension(uri, appContext.contentResolver)
                    val fileName = "${UUID.randomUUID()}-${System.currentTimeMillis()}.$fileExt"

                    val messageImageRef = messageRef.child(fileName)

                    val taskSnapShot = messageImageRef.putFile(uri).await()
                    val task = taskSnapShot.task
                    if (task.isComplete) {
                        val url = messageImageRef.downloadUrl.await()
                        val path = url?.toString() ?: ""
                        images = images + path
                    }
                }

                val message = Message(
                    mid = messageDocument.id,
                    from = tempState.user,
                    to = tempState.others,
                    groupId = groupId,
                    text = tempState.text,
                    images = images,
                    date = Date(System.currentTimeMillis()),
                    read = false
                )
                _state.update { it.copy(sending = false) }
                messageDocument.set(message).await()
                sendNotification(message)
            } catch (e: Exception) {
                _state.update { it.copy(sending = false) }
                e.printStackTrace()
                return@launch
            }
        }
    }

    private suspend fun sendNotification(message: Message) {
        val tokenDocumentRef = tokensCollection.document(message.to.uid)
        val tokenDocumentSnapShot = tokenDocumentRef.get().await()

        tokenDocumentSnapShot.toObject<FCMToken>()?.run {
            val postNotification = PushNotification(
                data = Notification(
                    title = message.from.name,
                    message = message.text.ifBlank { "Sent ${message.images.size} images" },
                    route = Screen.Chats.route
                ),
                to = token
            )
            notificationRepository.postNotification(postNotification)
        }
    }
}