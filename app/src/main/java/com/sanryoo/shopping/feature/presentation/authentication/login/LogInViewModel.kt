package com.sanryoo.shopping.feature.presentation.authentication.login

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.core.content.edit
import androidx.lifecycle.viewModelScope
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.sanryoo.shopping.feature.domain.google_client.GoogleAuthUiClient
import com.sanryoo.shopping.feature.domain.model.User
import com.sanryoo.shopping.feature.domain.model.fcmtoken.FCMToken
import com.sanryoo.shopping.feature.presentation._base_component.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val appContext: Application
) : BaseViewModel<LogInState, LogInUiEvent>(LogInState()) {

    private val usersCollectionRef = Firebase.firestore.collection("users")
    private val tokensCollectionRef = Firebase.firestore.collection("tokens")

    private val signInClient = GoogleAuthUiClient(
        context = appContext,
        oneTapClient = Identity.getSignInClient(appContext),
        auth = auth
    )

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

    fun onToggleVisiblePassword() {
        _state.update { it.copy(visiblePassword = !it.visiblePassword) }
    }

    fun logIn() {
        viewModelScope.launch {
            _state.update { it.copy(loadingButton = true) }
            onUiEvent(LogInUiEvent.HideHeyBoard)
            delay(300L)
            try {
                state.value.run {
                    val result = auth.signInWithEmailAndPassword(email, password).await()
                    if (result.user != null) {
                        if (result.user!!.isEmailVerified) {
                            _state.update { it.copy(loadingButton = false) }
                            handleLogInData()
                            onUiEvent(LogInUiEvent.BackToProfile)
                            return@launch
                        } else {
                            _state.update { it.copy(loadingButton = false) }
                            result.user!!.sendEmailVerification().await()
                            onUiEvent(LogInUiEvent.ShowSnackBar("Please verify your account in mail app and login again"))
                            return@launch
                        }
                    } else {
                        _state.update { it.copy(loadingButton = false) }
                        onUiEvent(LogInUiEvent.ShowSnackBar("Can not log in"))
                        return@launch
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(loadingButton = false) }
                onUiEvent(LogInUiEvent.ShowSnackBar(e.message.toString()))
                return@launch
            }
        }
    }

    suspend fun getSignInIntentSender(): IntentSender? {
        return signInClient.signIn()
    }

    fun logInWithGoogle(intent: Intent?) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(loadingScreen = true) }
                val result = signInClient.signInWithIntent(intent)
                if (result?.user != null) {
                    handleLogInData()
                    _state.update { it.copy(loadingScreen = false) }
                    onUiEvent(LogInUiEvent.BackToProfile)
                } else {
                    _state.update { it.copy(loadingScreen = false) }
                    onUiEvent(LogInUiEvent.ShowSnackBar("Can not log in with google"))
                }
            } catch (e: Exception) {
                _state.update { it.copy(loadingScreen = false) }
                onUiEvent(LogInUiEvent.ShowSnackBar("Can not log in with google"))
            }
        }
    }

    fun logInWithFacebook(context: Context) {
        viewModelScope.launch {
            val callbackManager = CallbackManager.Factory.create()
            val loginManager = LoginManager.getInstance()
            loginManager.registerCallback(callbackManager = callbackManager,
                callback = object : FacebookCallback<LoginResult> {
                    override fun onCancel() {
                        onUiEvent(LogInUiEvent.ShowSnackBar("Can not log in with Facebook"))
                        loginManager.unregisterCallback(callbackManager)
                    }

                    override fun onError(error: FacebookException) {
                        onUiEvent(LogInUiEvent.ShowSnackBar("Can not log in with Facebook"))
                        loginManager.unregisterCallback(callbackManager)
                    }

                    override fun onSuccess(result: LoginResult) {
                        handleFacebookAccessToken(result.accessToken)
                    }
                })
            loginManager.logInWithReadPermissions(
                activityResultRegistryOwner = context as ActivityResultRegistryOwner,
                callbackManager = callbackManager,
                permissions = listOf("email", "public_profile"),
            )
        }
    }

    private fun handleFacebookAccessToken(accessToken: AccessToken) {
        viewModelScope.launch {
            _state.update { it.copy(loadingScreen = true) }
            try {
                val credential = FacebookAuthProvider.getCredential(accessToken.token)
                val result = auth.signInWithCredential(credential).await()
                if (result.user != null) {
                    handleLogInData()
                    _state.update { it.copy(loadingScreen = false) }
                    onUiEvent(LogInUiEvent.BackToProfile)
                } else {
                    _state.update { it.copy(loadingScreen = false) }
                    onUiEvent(LogInUiEvent.ShowSnackBar("Can not log in with Facebook"))
                }
            } catch (e: Exception) {
                _state.update { it.copy(loadingScreen = false) }
                onUiEvent(LogInUiEvent.ShowSnackBar("Can not log in with Facebook"))
                return@launch
            }
        }
    }

    private suspend fun handleLogInData() {
        auth.currentUser?.run {

            //Set FCM Token to tokens Collection
            val shoppingSF = appContext.getSharedPreferences("shopping_sf", Context.MODE_PRIVATE)
            val token = shoppingSF.getString("fcm_token", "") ?: ""

            val tokenDocumentRef = tokensCollectionRef.document(uid)
            tokenDocumentRef.set(FCMToken(token)).await()

            //Handle First times log in
            val userDocumentRef = usersCollectionRef.document(uid)
            val documentSnapshot = userDocumentRef.get().await()
            val result = documentSnapshot.toObject<User>()
            if (result == null) { // is First times log in

                // get photoUrl for profile picture
                var photo = photoUrl?.toString() ?: ""
                if (photoUrl.toString().contains("graph.facebook.com")) {
                    photo += "?type=large&access_token=${AccessToken.getCurrentAccessToken()?.token}"
                }

                // get name
                val defaultName = email?.substringBefore("@") ?: ""
                val name = displayName ?: defaultName

                val initUser = User(
                    uid = uid,
                    name = name,
                    email = email ?: "",
                    profilePicture = photo
                )

                userDocumentRef.set(initUser).await()
            }
        }
    }
}
