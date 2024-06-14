package com.sanryoo.shopping.feature.presentation.using.my_account

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sanryoo.shopping.feature.domain.model.User
import com.sanryoo.shopping.feature.presentation._base_component.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : BaseViewModel<UserState, UserUiEvent>(UserState()) {

    private val usersCollectionRef = Firebase.firestore.collection("users")
    private val imagesStorageRef = Firebase.storage.reference.child("users")

    init {
        getInformationUser()
    }

    private fun getInformationUser() {
        viewModelScope.launch {
            auth.currentUser?.run {
                val userDocumentRef = usersCollectionRef.document(uid)

                val documentSnapShot = userDocumentRef.get().await()
                documentSnapShot.toObject<User>()?.let { user ->
                    _state.update { it.copy(user = user) }
                }
            }
        }
    }

    fun setShowDialogChangeChangeName(status: Boolean) {
        _state.update { it.copy(changeName = status) }
    }

    fun onChangeName(newValue: String) {
        if (newValue.length < 30) {
            _state.update { it.copy(user = state.value.user.copy(name = newValue)) }
        }
    }

    fun setShowDialogChangeBio(status: Boolean) {
        _state.update { it.copy(changeBio = status) }
    }

    fun onChangeBio(newValue: String) {
        if (newValue.length < 200) {
            _state.update { it.copy(user = state.value.user.copy(bio = newValue)) }
        }
    }

    fun setShowDialogChangePhoneNumber(status: Boolean) {
        _state.update { it.copy(changePhoneNumber = status) }
    }

    fun onChangePhoneNumber(newValue: String) {
        if (newValue.length < 15) {
            _state.update { it.copy(user = state.value.user.copy(phoneNumber = newValue)) }
        }
    }

    fun setShowDialogChangeAddress(status: Boolean) {
        _state.update { it.copy(changeAddress = status) }
    }

    fun onChangeAddress(newValue: String) {
        if (newValue.length < 200) {
            _state.update { it.copy(user = state.value.user.copy(address = newValue)) }
        }
    }

    fun setChooseImageFor(chooseImage: ChooseImage) {
        _state.update { it.copy(chooseImage = chooseImage) }
    }

    fun setShowBottomSheet(status: Boolean) {
        onUiEvent(UserUiEvent.SetShowBottomSheet(status))
    }

    fun setProfilePicture(uri: Uri?) {
        _state.update { it.copy(profilePicture = uri) }
    }

    fun setCoverPhoto(uri: Uri?) {
        _state.update { it.copy(coverPhoto = uri) }
    }

    fun updateInformation() {
        viewModelScope.launch {
            auth.currentUser?.run {

                if (state.value.profilePicture != null || state.value.coverPhoto != null) {
                    _state.update { it.copy(isLoading = true) }
                }

                //If profile picture change
                state.value.profilePicture?.let { uri ->
                    val profilePictureRef = imagesStorageRef.child("$uid/profile-picture")

                    val taskSnapshot = profilePictureRef.putFile(uri).await()
                    val task = taskSnapshot.task

                    if (task.isComplete) {
                        val url = profilePictureRef.downloadUrl.await()
                        val path = url?.toString() ?: ""
                        _state.update { it.copy(user = it.user.copy(profilePicture = path)) }
                    }
                }

                // If cover photo change
                state.value.coverPhoto?.let { uri ->
                    val coverPhotoRef = imagesStorageRef.child("$uid/cover-photo")

                    val taskSnapshot = coverPhotoRef.putFile(uri).await()
                    val task = taskSnapshot.task

                    if (task.isComplete) {
                        val url = coverPhotoRef.downloadUrl.await()
                        val path = url?.toString() ?: ""
                        _state.update { it.copy(user = it.user.copy(coverPhoto = path)) }
                    }
                }

                val userDocumentRef = usersCollectionRef.document(uid)
                userDocumentRef.set(state.value.user)

                _state.update { it.copy(isLoading = false) }
                onUiEvent(UserUiEvent.BackToProfile)
            }
        }
    }

}