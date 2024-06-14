package com.sanryoo.shopping.feature.presentation.using.message

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.isGranted
import com.sanryoo.shopping.R
import com.sanryoo.shopping.feature.presentation._component.bottomsheet.ChooseImages
import com.sanryoo.shopping.feature.util.dateFormat
import com.sanryoo.shopping.ui.theme.Primary
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@ExperimentalMaterialApi
@Composable
fun MessageScreen(
    othersId: String,
    navController: NavHostController,
    viewModel: MessageViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val sheetState = rememberModalBottomSheetState(
        initialValue = Hidden,
        animationSpec = tween(500)
    )

    LaunchedEffect(Unit) {
        viewModel.getOthers(othersId)
        viewModel.getMessages(othersId)
    }
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is MessageUIEvent.ClearFocus -> {
                    focusManager.clearFocus()
                }

                is MessageUIEvent.ScrollToFirstItem -> {
                    listState.animateScrollToItem(0)
                }

                is MessageUIEvent.BackToPrevScreen -> {
                    navController.popBackStack()
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    MessageContent(
        sheetState = sheetState,
        listState = listState,
        state = state,
        onTextChange = viewModel::onTextChange,
        onImagesChange = viewModel::onImagesChange,
        onSendMessage = viewModel::onSendMessage,
        onBack = { viewModel.onUiEvent(MessageUIEvent.BackToPrevScreen) },
        onClearFocus = { viewModel.onUiEvent(MessageUIEvent.ClearFocus) },
    )
}

@ExperimentalMaterialApi
@Composable
private fun MessageContent(
    sheetState: ModalBottomSheetState,
    listState: LazyListState,
    state: MessageState = MessageState(),
    onTextChange: (String) -> Unit = {},
    onImagesChange: (List<Uri>) -> Unit = {},
    onSendMessage: () -> Unit = {},
    onClearFocus: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = {
            onImagesChange(state.images + it)
        }
    )
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = onClearFocus
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.White.copy(alpha = 0.4f)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Icon back",
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(start = 5.dp)
                    .size(30.dp)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = onBack
                    ),
                tint = Primary
            )
            AsyncImage(
                model = state.others.profilePicture,
                contentDescription = "Others profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(start = 5.dp, end = 10.dp)
                    .clip(CircleShape)
                    .size(50.dp)
            )
            Text(
                text = state.others.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            reverseLayout = true,
            state = listState
        ) {
            if (state.sending) {
                item {
                    Text(
                        text = "Sending",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        textAlign = TextAlign.End,
                        fontSize = 14.sp
                    )
                }
            }
            itemsIndexed(state.messages) { index, message ->
                val isOwnedMessage = message.from.uid == state.user.uid
                val showDate =
                    try {
                        index == state.messages.size - 1 ||
                                TimeUnit.MILLISECONDS.toMinutes(message.date!!.time - state.messages[index + 1].date!!.time) > 5
                    } catch (_: Exception) {
                        false
                    }
                if (message.text.isNotBlank()) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .align(if (isOwnedMessage) Alignment.CenterEnd else Alignment.CenterStart)
                                .padding(
                                    top = 2.dp,
                                    bottom = 2.dp,
                                    start = if (isOwnedMessage) 100.dp else 5.dp,
                                    end = if (isOwnedMessage) 5.dp else 100.dp
                                )
                                .clip(RoundedCornerShape(15.dp))
                                .background(if (isOwnedMessage) Primary else MaterialTheme.colors.surface)
                                .blur(25.dp)
                        ) {
                            Text(
                                text = message.text,
                                modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),
                                fontSize = 16.sp,
                                color = if (isOwnedMessage) Color.White else Color.Black
                            )
                        }
                    }
                }
                message.images.asReversed().forEach {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        AsyncImage(
                            model = it,
                            contentDescription = "Image",
                            modifier = Modifier
                                .align(if (isOwnedMessage) Alignment.CenterEnd else Alignment.CenterStart)
                                .padding(5.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .fillParentMaxWidth(0.6f)
                        )
                    }
                }
                if (showDate && message.date != null) {
                    Text(
                        text = dateFormat.format(message.date!!),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(3.dp)
                    )
                }
            }
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            if (state.images.isNotEmpty()) {
                Divider(Modifier.fillMaxWidth())
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentPadding = PaddingValues(5.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    itemsIndexed(state.images) { index, photoUrl ->
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(1f)
                        ) {
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = "Image comment",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colors.background)
                                    .border(1.dp, Color.Red, CircleShape)
                                    .clickable(interactionSource = MutableInteractionSource(),
                                        indication = null,
                                        onClick = {
                                            val tempList = state.images.toMutableList()
                                            tempList.removeAt(index)
                                            onImagesChange(tempList)
                                        }
                                    )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.close),
                                    contentDescription = "Icon close",
                                    tint = Color.Red,
                                    modifier = Modifier
                                        .padding(7.dp)
                                        .size(10.dp)
                                )
                            }
                        }
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                Icon(
                    painter = painterResource(id = R.drawable.image),
                    contentDescription = "Icon Image",
                    modifier = Modifier
                        .padding(start = 8.dp, bottom = 13.dp, end = 5.dp)
                        .size(28.dp)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = {
                                scope.launch {
                                    launcher.launch(PickVisualMediaRequest(ImageOnly))
                                }
                            }
                        ),
                    tint = Primary
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(5.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colors.surface)
                ) {
                    BasicTextField(
                        value = state.text,
                        onValueChange = onTextChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterStart)
                            .padding(horizontal = 15.dp, vertical = 10.dp),
                        textStyle = TextStyle(fontSize = 16.sp),
                        maxLines = 5
                    )
                    if (state.text.isEmpty()) {
                        Text(
                            text = "Aa",
                            fontSize = 16.sp,
                            color = Color.Black.copy(0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterStart)
                                .padding(horizontal = 15.dp, vertical = 10.dp)
                        )
                    }
                }
                Icon(
                    painter = painterResource(id = R.drawable.send),
                    contentDescription = "Icon Send",
                    modifier = Modifier
                        .padding(end = 8.dp, bottom = 13.dp, start = 5.dp)
                        .size(28.dp)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = onSendMessage
                        ),
                    tint = Primary
                )
            }
        }
    }
}