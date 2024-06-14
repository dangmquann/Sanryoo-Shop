package com.sanryoo.shopping.feature.presentation._component.bottomsheet

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sanryoo.shopping.R
import com.sanryoo.shopping.feature.util.ProductConstant
import com.sanryoo.shopping.ui.theme.Primary

@Composable
fun ChooseImages(
    maxImages: Int = ProductConstant.MAX_IMAGES,
    currentSize: Int = 0,
    currentImages: List<Uri> = emptyList(),
    hideBottomSheet: () -> Unit = {},
    onChangeImages: (List<Uri>) -> Unit = {},
) {
    var mediaList by remember {
        mutableStateOf(emptyList<Uri>())
    }

    var selectedImages by remember {
        mutableStateOf(emptyList<Uri>())
    }

    val queryUri: Uri = MediaStore.Files.getContentUri("external")
    val projection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.MEDIA_TYPE,
        MediaStore.Files.FileColumns.DATE_ADDED
    )
    val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
    val selectionArgs = arrayOf(
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
    )
    val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        context.contentResolver.query(
            queryUri, projection, selection, selectionArgs, sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri: Uri = ContentUris.withAppendedId(queryUri, id)
                mediaList = mediaList + contentUri
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .size(30.dp)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = hideBottomSheet
                    ),
                painter = painterResource(id = R.drawable.close),
                contentDescription = "Icon close"
            )
            Text(
                text = "Choose Images", fontSize = 26.sp, modifier = Modifier.weight(1f)
            )
            Text(
                text = "${selectedImages.size + currentSize}/$maxImages",
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Text(text = "Add",
                fontSize = 20.sp,
                color = Primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .clickable(interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = {
                            onChangeImages(currentImages + selectedImages)
                            selectedImages = emptyList()
                            hideBottomSheet()
                        }
                    )
            )
        }
        Divider(modifier = Modifier.fillMaxWidth())
        LazyVerticalGrid(columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
            content = {
                items(mediaList) { uri ->
                    Box(modifier = Modifier
                        .aspectRatio(1f)
                        .clickable(interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = {
                                if (!selectedImages.contains(uri)) {
                                    if (selectedImages.size + currentSize < maxImages) {
                                        selectedImages = selectedImages + uri
                                    }
                                } else {
                                    val tempImageList = selectedImages.toMutableList()
                                    tempImageList.remove(uri)
                                    selectedImages = tempImageList
                                }
                            })) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        if (selectedImages.contains(uri)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colors.onBackground.copy(0.2f))
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(5.dp)
                                    .size(25.dp)
                                    .border(2.dp, Color.White, CircleShape)
                                    .clip(CircleShape)
                                    .background(Primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.tick),
                                    contentDescription = "Icon check",
                                    modifier = Modifier.size(15.dp),
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}