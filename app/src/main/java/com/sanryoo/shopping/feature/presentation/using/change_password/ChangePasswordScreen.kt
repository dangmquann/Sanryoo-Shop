package com.sanryoo.shopping.feature.presentation.using.change_password

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.sanryoo.shopping.R
import com.sanryoo.shopping.ui.theme.Primary
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ChangePasswordScreen(
    navHostController: NavHostController,
    scaffoldState: ScaffoldState,
    viewModel: ChangePasswordViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is ChangePasswordUiEvent.HideKeyBoard -> {
                    focusManager.clearFocus()
                }

                is ChangePasswordUiEvent.BackToProfile -> {
                    navHostController.popBackStack()
                }

                is ChangePasswordUiEvent.ShowSnackBar -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    ChangePasswordContent(
        state = state,
        onOldPasswordChange = viewModel::onOldPasswordChange,
        onNewPasswordChange = viewModel::onNewPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onToggleVisibleOldPassword = viewModel::onToggleVisibleOldPassword,
        onToggleVisibleNewPassword = viewModel::onToggleVisibleNewPassword,
        onToggleVisibleConfirmPassword = viewModel::onToggleVisibleConfirmPassword,
        onBack = { viewModel.onUiEvent(ChangePasswordUiEvent.BackToProfile) },
        onHideKeyBoard = { viewModel.onUiEvent(ChangePasswordUiEvent.HideKeyBoard) },
        onChangePassword = viewModel::changePassword
    )
}

@Composable
private fun ChangePasswordContent(
    state: ChangePasswordState = ChangePasswordState(),
    onOldPasswordChange: (String) -> Unit = {},
    onNewPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    onToggleVisibleOldPassword: () -> Unit = {},
    onToggleVisibleNewPassword: () -> Unit = {},
    onToggleVisibleConfirmPassword: () -> Unit = {},
    onBack: () -> Unit = {},
    onHideKeyBoard: () -> Unit = {},
    onChangePassword: () -> Unit = {}
) {
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Icon Back",
                modifier = Modifier
                    .padding(top = 5.dp, start = 5.dp)
                    .size(40.dp)
                    .align(Alignment.CenterStart)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = onBack
                    ),
                tint = Primary
            )
            Text(
                text = "Change your password",
                fontSize = 24.sp,
                color = Primary,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(120.dp))
            OutlinedTextField(
                value = state.oldPassword,
                onValueChange = onOldPasswordChange,
                label = { Text(text = "Old Password", fontSize = 16.sp) },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(vertical = 5.dp),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                trailingIcon = {
                    IconButton(onClick = onToggleVisibleOldPassword) {
                        Icon(
                            painter = painterResource(id = if (state.visibleOldPassword) R.drawable.visibile else R.drawable.unvisibile),
                            contentDescription = "Visible old password",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                },
                visualTransformation = if (state.visibleOldPassword) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true
            )
            OutlinedTextField(
                value = state.newPassword,
                onValueChange = onNewPasswordChange,
                label = { Text(text = "New Password", fontSize = 16.sp) },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(vertical = 5.dp),
                shape = RoundedCornerShape(10.dp),
                trailingIcon = {
                    IconButton(onClick = onToggleVisibleNewPassword) {
                        Icon(
                            painter = painterResource(id = if (state.visibleNewPassword) R.drawable.visibile else R.drawable.unvisibile),
                            contentDescription = "Visible password",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                },
                visualTransformation = if (state.visibleNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true
            )
            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = { Text(text = "Confirm password", fontSize = 16.sp) },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(vertical = 5.dp),
                shape = RoundedCornerShape(10.dp),
                trailingIcon = {
                    IconButton(onClick = onToggleVisibleConfirmPassword) {
                        Icon(
                            painter = painterResource(id = if (state.visibleConfirmPassword) R.drawable.visibile else R.drawable.unvisibile),
                            contentDescription = "Visible confirm password",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                },
                visualTransformation = if (state.visibleConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                singleLine = true
            )
            Button(
                onClick = onChangePassword,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(vertical = 5.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(20.dp),
                enabled = state.oldPassword.isNotBlank() && state.newPassword.isNotBlank() && state.confirmPassword.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    disabledBackgroundColor = Primary,
                    disabledContentColor = Color.White
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(30.dp)
                    )
                } else {
                    Text(text = "Change password", fontSize = 16.sp)
                }
            }
        }
    }
}