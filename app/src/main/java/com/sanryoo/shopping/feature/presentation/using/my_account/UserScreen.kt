package com.sanryoo.shopping.feature.presentation.using.my_account

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.sanryoo.shopping.R
import com.sanryoo.shopping.feature.presentation._component.shimmerEffect
import com.sanryoo.shopping.feature.presentation.using.my_account.component.ChangeInformationDialog
import com.sanryoo.shopping.feature.presentation._component.bottomsheet.ChooseImage
import com.sanryoo.shopping.feature.presentation._component.readImagePermission
import com.sanryoo.shopping.feature.presentation.using.my_account.component.ItemUser
import com.sanryoo.shopping.ui.theme.Primary
import kotlinx.coroutines.flow.collectLatest

@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun UserScreen(
    navController: NavHostController,
    viewModel: UserViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState(
        initialValue = Hidden,
        animationSpec = tween(500),
        confirmStateChange = { it != HalfExpanded }
    )
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is UserUiEvent.BackToProfile -> {
                    navController.popBackStack()
                }

                is UserUiEvent.SetShowBottomSheet -> {
                    sheetState.animateTo(if (event.isShow) Expanded else Hidden)
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    UserContent(
        sheetState = sheetState,
        state = state,
        onChangeName = viewModel::onChangeName,
        setShowDialogChangeName = viewModel::setShowDialogChangeChangeName,
        onChangeBio = viewModel::onChangeBio,
        setShowDialogChangeBio = viewModel::setShowDialogChangeBio,
        onChangePhoneNumber = viewModel::onChangePhoneNumber,
        setShowDialogChangePhoneNumber = viewModel::setShowDialogChangePhoneNumber,
        onChangeAddress = viewModel::onChangeAddress,
        setShowDialogChangeAddress = viewModel::setShowDialogChangeAddress,
        setChooseImage = viewModel::setChooseImageFor,
        setShowBottomSheet = viewModel::setShowBottomSheet,
        setProfilePictureUri = viewModel::setProfilePicture,
        setCoverPhotoUri = viewModel::setCoverPhoto,
        onBack = { viewModel.onUiEvent(UserUiEvent.BackToProfile) },
        onSave = viewModel::updateInformation,
    )
}

@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
private fun UserContent(
    sheetState: ModalBottomSheetState,
    state: UserState = UserState(),
    onChangeName: (String) -> Unit = {},
    setShowDialogChangeName: (Boolean) -> Unit = {},
    onChangeBio: (String) -> Unit = {},
    setShowDialogChangeBio: (Boolean) -> Unit = {},
    onChangePhoneNumber: (String) -> Unit = {},
    setShowDialogChangePhoneNumber: (Boolean) -> Unit = {},
    onChangeAddress: (String) -> Unit = {},
    setShowDialogChangeAddress: (Boolean) -> Unit = {},
    setChooseImage: (ChooseImage) -> Unit = {},
    setShowBottomSheet: (Boolean) -> Unit = {},
    setProfilePictureUri: (Uri?) -> Unit = {},
    setCoverPhotoUri: (Uri?) -> Unit = {},
    onBack: () -> Unit = {},
    onSave: () -> Unit = {},
) {
    if (state.isLoading) {
        AlertDialog(
            onDismissRequest = {},
            backgroundColor = Color.Transparent,
            contentColor = Primary,
            buttons = {
                CircularProgressIndicator(modifier = Modifier.size(50.dp), strokeWidth = 4.dp)
            }
        )
    }

    val readImagePermission = readImagePermission(
        onPermissionGranted = {
            setShowBottomSheet(true)
        }
    )

    //Dialog for change information
    ChangeInformationDialog(
        visible = state.changeName,
        label = "Change Name",
        originalContent = state.user.name,
        onChange = onChangeName,
        onDismiss = setShowDialogChangeName,
        maxLengthContent = 30,
    )
    ChangeInformationDialog(
        visible = state.changeBio,
        label = "Change Bio",
        originalContent = state.user.bio,
        onChange = onChangeBio,
        onDismiss = setShowDialogChangeBio,
        maxLines = 4,
        maxLengthContent = 200
    )
    ChangeInformationDialog(
        visible = state.changePhoneNumber,
        label = "Change Phone Number",
        originalContent = state.user.phoneNumber,
        onChange = onChangePhoneNumber,
        onDismiss = setShowDialogChangePhoneNumber,
        maxLengthContent = 15
    )
    ChangeInformationDialog(
        visible = state.changeAddress,
        label = "Change Address",
        originalContent = state.user.address,
        onChange = onChangeAddress,
        onDismiss = setShowDialogChangeAddress,
        maxLengthContent = 200
    )
    ModalBottomSheetLayout(
        modifier = Modifier.statusBarsPadding(),
        sheetState = sheetState,
        sheetBackgroundColor = MaterialTheme.colors.background,
        sheetElevation = 0.dp,
        sheetContent = {
            ChooseImage(
                hideBottomSheet = { setShowBottomSheet(false) },
                onChooseImage = { uri ->
                    when (state.chooseImage) {
                        ChooseImage.PROFILE_PICTURE -> {
                            setProfilePictureUri(uri)
                        }

                        ChooseImage.COVER_PHOTO -> {
                            setCoverPhotoUri(uri)
                        }
                    }
                }
            )
        },
    ) {
        Scaffold(
            topBar = {
                Surface(
                    color = MaterialTheme.colors.background,
                    contentColor = MaterialTheme.colors.onBackground,
                    elevation = 16.dp
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Icon Back",
                            modifier = Modifier
                                .padding(10.dp)
                                .size(30.dp)
                                .align(Alignment.CenterStart)
                                .clickable(
                                    interactionSource = MutableInteractionSource(),
                                    indication = null,
                                    onClick = onBack
                                ),
                            tint = Primary
                        )
                        Text(
                            text = "My Account",
                            fontSize = 24.sp,
                            color = Primary,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        TextButton(
                            onClick = onSave,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 5.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "Save",
                                fontSize = 16.sp,
                                color = Primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                ) {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 40.dp)
                        .background(MaterialTheme.colors.surface)
                        .clickable {
                            setChooseImage(ChooseImage.COVER_PHOTO)
                            if (readImagePermission.status.isGranted) {
                                setShowBottomSheet(true)
                            } else {
                                readImagePermission.launchPermissionRequest()
                            }
                        }
                    ) {
                        AsyncImage(
                            model = if (state.coverPhoto != null)
                                state.coverPhoto
                            else
                                state.user.coverPhoto,
                            contentDescription = "Cover Photo",
                            modifier = if (state.user.coverPhoto.isBlank()) {
                                Modifier.fillMaxSize()
                            } else {
                                Modifier
                                    .fillMaxSize()
                                    .shimmerEffect()
                            },
                            contentScale = ContentScale.Crop
                        )
                        Row(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .background(
                                    MaterialTheme.colors.background.copy(alpha = 0.3f),
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(5.dp), verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.edit),
                                contentDescription = "Icon pencil",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "Edit", fontSize = 18.sp)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .size(120.dp)
                            .align(Alignment.BottomStart)
                            .background(MaterialTheme.colors.background, CircleShape)
                            .clip(CircleShape)
                            .clickable {
                                setChooseImage(ChooseImage.PROFILE_PICTURE)
                                if (readImagePermission.status.isGranted) {
                                    setShowBottomSheet(true)
                                } else {
                                    readImagePermission.launchPermissionRequest()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = if (state.profilePicture != null)
                                state.profilePicture
                            else
                                state.user.profilePicture.ifBlank { R.drawable.user },
                            contentDescription = "Profile picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .shimmerEffect()
                        )
                        Row(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colors.surface.copy(alpha = 0.3f),
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(5.dp), verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.edit),
                                contentDescription = "Icon pencil",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "Edit", fontSize = 18.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                ItemUser(label = "Name",
                    content = state.user.name,
                    onClick = { setShowDialogChangeName(true) })
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp)
                )
                ItemUser(label = "Bio",
                    content = state.user.bio,
                    onClick = { setShowDialogChangeBio(true) })
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp)
                )
                ItemUser(
                    label = "Email",
                    content = state.user.email,
                    showIconNext = false,
                    enable = false
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp)
                )
                ItemUser(label = "Phone Number",
                    content = state.user.phoneNumber,
                    onClick = { setShowDialogChangePhoneNumber(true) })
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp)
                )
                ItemUser(label = "Address",
                    content = state.user.address,
                    onClick = { setShowDialogChangeAddress(true) })
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp)
                )
            }
        }
    }
    BackHandler(
        enabled = sheetState.isVisible,
        onBack = { setShowBottomSheet(false) }
    )
}
