package com.sanryoo.shopping.feature.presentation.authentication.login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.sanryoo.shopping.R
import com.sanryoo.shopping.feature.util.Screen
import com.sanryoo.shopping.ui.theme.Primary
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun LogInScreen(
    navController: NavHostController,
    scaffoldState: ScaffoldState,
    viewModel: LogInViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.logInWithGoogle(result.data)
            } else {
                viewModel.onUiEvent(LogInUiEvent.ShowSnackBar("Can not log in with Google"))
            }
        }
    )
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is LogInUiEvent.HideHeyBoard -> {
                    focusManager.clearFocus()
                }
                is LogInUiEvent.NavigateToSignUp -> {
                    navController.navigate(Screen.SignUp.route)
                }
                is LogInUiEvent.BackToProfile -> {
                    navController.popBackStack()
                }
                is LogInUiEvent.ShowSnackBar -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    LogInContent(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onToggleVisiblePassword = viewModel::onToggleVisiblePassword,
        onHideKeyBoard = { viewModel.onUiEvent(LogInUiEvent.HideHeyBoard) },
        onBack = { viewModel.onUiEvent(LogInUiEvent.BackToProfile) },
        onLogIn = viewModel::logIn,
        onLogInWithGoogle = {
            scope.launch {
                val signInIntentSender = viewModel.getSignInIntentSender()
                launcher.launch(
                    IntentSenderRequest
                        .Builder(signInIntentSender ?: return@launch)
                        .build()
                )
            }
        },
        onLogInWithFacebook = { viewModel.logInWithFacebook(context) },
        onCreateNewAccount = { viewModel.onUiEvent(LogInUiEvent.NavigateToSignUp) },
    )
}

@Composable
private fun LogInContent(
    state: LogInState = LogInState(),
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onToggleVisiblePassword: () -> Unit = {},
    onHideKeyBoard: () -> Unit = {},
    onBack: () -> Unit = {},
    onLogIn: () -> Unit = {},
    onLogInWithGoogle: () -> Unit = {},
    onLogInWithFacebook: () -> Unit = {},
    onCreateNewAccount: () -> Unit = {},
) {
    if (state.loadingScreen) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                strokeWidth = 3.dp,
                modifier = Modifier.size(40.dp)
            )
        }
    } else {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .fillMaxSize()
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                    onClick = onHideKeyBoard
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Icon Back",
                modifier = Modifier
                    .padding(top = 5.dp, start = 5.dp)
                    .size(40.dp)
                    .align(Alignment.Start)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = onBack
                    ),
                tint = Primary
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_shopping_circle),
                    contentDescription = "Logo Shopping Circle",
                    modifier = Modifier
                        .padding(bottom = 60.dp)
                        .size(70.dp)
                        .clip(CircleShape)
                )
                OutlinedTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    label = { Text(text = "Email", fontSize = 16.sp) },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(vertical = 5.dp),
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    maxLines = 1
                )
                OutlinedTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    label = { Text(text = "Password", fontSize = 16.sp) },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(vertical = 5.dp),
                    shape = RoundedCornerShape(10.dp),
                    trailingIcon = {
                        IconButton(onClick = onToggleVisiblePassword) {
                            Icon(
                                painter = painterResource(id = if (state.visiblePassword) R.drawable.visibile else R.drawable.unvisibile),
                                contentDescription = "Visible password",
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    },
                    visualTransformation = if (state.visiblePassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    maxLines = 1
                )
                Button(
                    onClick = onLogIn,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(vertical = 5.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(20.dp),
                    enabled = state.email.isNotBlank() && state.password.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        disabledBackgroundColor = Primary,
                        disabledContentColor = Color.White
                    )
                ) {
                    if (state.loadingButton) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(30.dp)
                        )
                    } else {
                        Text(text = "Log in", fontSize = 16.sp)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(top = 60.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = "Logo google",
                        modifier = Modifier
                            .size(45.dp)
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick = onLogInWithGoogle,
                            )
                    )
                    Text(text = "or", modifier = Modifier.padding(horizontal = 25.dp))
                    Image(
                        painter = painterResource(id = R.drawable.facebook),
                        contentDescription = "Logo google",
                        modifier = Modifier
                            .size(45.dp)
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick = onLogInWithFacebook
                            )
                    )
                }
            }
            Text(
                text = "Create new account",
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(vertical = 30.dp)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = onCreateNewAccount
                    )
            )
        }
    }
}
