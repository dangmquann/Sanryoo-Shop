package com.sanryoo.shopping.feature.presentation.using.notifications

import android.app.Application
import android.app.NotificationManager
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.sanryoo.shopping.feature.domain.model.notification.Notification
import com.sanryoo.shopping.feature.presentation._base_component.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val appContext: Application
) : BaseViewModel<NotificationState, NotificationsUIEvent>(NotificationState()) {

    private val notificationsCollectionRef = Firebase.firestore.collection("notifications")

    private var notificationsListener: ListenerRegistration? = null

    init {
        getAllNotification()
        markAllAsSeen()
    }

    private fun getAllNotification() {
        notificationsListener?.remove()
        auth.currentUser?.run {
            notificationsListener = notificationsCollectionRef
                .whereEqualTo("to", uid)
                .addSnapshotListener { querySnapShot, error ->
                    error?.let { e ->
                        e.printStackTrace()
                        return@addSnapshotListener
                    }
                    querySnapShot?.let { snapShot ->
                        val notifications = snapShot.documents
                            .mapNotNull { it.toObject<Notification>() }
                            .sortedByDescending { it.date }
                        _state.update { it.copy(notifications = notifications) }
                    }
                }
        }
    }

    private fun markAllAsSeen() {

        val notificationManager = appContext.getSystemService(NotificationManager::class.java)
        notificationManager.cancelAll()

        auth.currentUser?.run {
            viewModelScope.launch {
                val notificationQuery = notificationsCollectionRef
                    .whereEqualTo("to", uid)
                    .get()
                    .await()

                notificationQuery.documents.onEach { documentSnapshot ->
                    notificationsCollectionRef.document(documentSnapshot.id)
                        .update("seen", true)
                        .await()
                }
            }
        }
    }

    fun markAllAsRead() {
        auth.currentUser?.run {
            viewModelScope.launch {
                val notificationQuery = notificationsCollectionRef
                    .whereEqualTo("to", uid)
                    .get()
                    .await()

                notificationQuery.documents.onEach { documentSnapshot ->
                    notificationsCollectionRef.document(documentSnapshot.id)
                        .update("read", true)
                        .await()
                }
            }
        }
    }

    fun onClickNotification(notification: Notification) {
        viewModelScope.launch {
            if (!notification.read) {
                val document = notificationsCollectionRef.document(notification.nid)
                document.update("read", true).await()
            }
            onUiEvent(NotificationsUIEvent.NavigateToRoute(notification.route))
        }
    }

}