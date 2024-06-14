package com.sanryoo.shopping.feature.presentation.authentication.signup

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sanryoo.shopping.feature.presentation._base_component.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : BaseViewModel<SignUpState, SignUpUiEvent>(SignUpState()) {

    fun onEmailChange(newValue: String) {
        if (newValue.length < 40) {
            _state.update { it.copy(email = newValue) }
        }
    }

    fun onPasswordChange(newValue: String) {
        if (newValue.length < 40) {
            _state.update { it.copy(password = newValue) }
        }
    }

    fun onConfirmPasswordChange(newValue: String) {
        if (newValue.length < 40) {
            _state.update { it.copy(confirmPassword = newValue) }
        }
    }

    fun onToggleVisiblePassword() {
        _state.update { it.copy(visiblePassword = !it.visiblePassword) }
    }

    fun onToggleVisibleConfirmPassword() {
        _state.update { it.copy(visibleConfirmPassword = !it.visibleConfirmPassword) }
    }

    fun onSignIn() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            onUiEvent(SignUpUiEvent.HideKeyBoard)
            delay(300L)
            if (state.value.password.length < 15) {
                _state.update { it.copy(isLoading = false) }
                onUiEvent(SignUpUiEvent.ShowSnackBar("Password must be longer than 15 character"))
                return@launch
            } else if (state.value.confirmPassword != state.value.password) {
                _state.update { it.copy(isLoading = false) }
                onUiEvent(SignUpUiEvent.ShowSnackBar("Confirm password does not match"))
                return@launch
            }
            try {
                state.value.run {
                    val result = auth.createUserWithEmailAndPassword(email, password).await()
                    if (result.user != null) {
                        auth.signOut()
                        onUiEvent(SignUpUiEvent.BackToPrevScreen)
                        onUiEvent(SignUpUiEvent.ShowSnackBar("Sign up successful"))
                    } else {
                        onUiEvent(SignUpUiEvent.ShowSnackBar("Can not create account"))
                    }
                    _state.update { it.copy(isLoading = false) }
                    return@launch
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                onUiEvent(SignUpUiEvent.ShowSnackBar(e.message.toString()))
                return@launch
            }
        }
    }
}