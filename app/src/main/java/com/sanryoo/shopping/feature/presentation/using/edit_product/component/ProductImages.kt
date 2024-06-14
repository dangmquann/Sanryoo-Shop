package com.sanryoo.shopping.feature.presentation.using.edit_product.component

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.sanryoo.shopping.R
import com.sanryoo.shopping.feature.presentation._component.readImagePermission
import com.sanryoo.shopping.feature.presentation.using.edit_product.SheetContent
import com.sanryoo.shopping.feature.presentation.using.edit_product.SheetContent.IMAGES
import com.sanryoo.shopping.ui.theme.Primary

@ExperimentalPermissionsApi
@ExperimentalFoundationApi
@Composable
fun ProductImages(
    modifier: Modifier = Modifier,
    existImages: List<String> = emptyList(),
    onChangeExistImages: (List<String>) -> Unit = {},
    images: List<Uri> = emptyList(),
    onChangeImages: (List<Uri>) -> Unit = {},
    setSheetContent: (SheetContent) -> Unit = {},
    setShowBottomSheet: (Boolean) -> Unit = {}
) {
    val readImagePermission = readImagePermission(
        onPermissionGranted = {
            setSheetContent(IMAGES)
            setShowBottomSheet(true)
        }
    )
    val height = LocalConfiguration.current.screenWidthDp.dp / 4
    LazyVerticalStaggeredGrid(modifier = modifier
        .height(height * (1 + images.size / 4))
        .background(MaterialTheme.colors.background),
        columns = StaggeredGridCells.Fixed(4),
        content = {
            itemsIndexed(existImages) { index, url ->
                ItemProductImage(
                    model = url,
                    onDelete = {
                        val tempList = existImages.toMutableList()
                        tempList.removeAt(index)
                        onChangeExistImages(tempList)
                    }
                )
            }
            itemsIndexed(images) { index, uri ->
                ItemProductImage(
                    model = uri,
                    onDelete = {
                        val tempList = images.toMutableList()
                        tempList.removeAt(index)
                        onChangeImages(tempList)
                    }
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(5.dp))
                        .padding(10.dp)
                        .border(
                            width = 1.dp, color = Primary, shape = RoundedCornerShape(5.dp)
                        )
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = {
                                if (readImagePermission.status.isGranted) {
                                    setSheetContent(IMAGES)
                                    setShowBottomSheet(true)
                                } else {
                                    setShowBottomSheet(false)
                                    readImagePermission.launchPermissionRequest()
                                }
                            }), contentAlignment = Alignment.Center
                ) {
                    Text(text = "+ Add", color = Primary)
                }
            }
        }
    )
}

@Composable
private fun ItemProductImage(
    model: Any?,
    onDelete: () -> Unit = {}
) {
    Box {
        AsyncImage(
            model = model,
            contentDescription = "Image",
            modifier = Modifier
                .padding(10.dp)
                .aspectRatio(1f)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .border(
                    width = 1.dp, color = Primary, shape = CircleShape
                )
                .background(MaterialTheme.colors.background)
                .clickable(onClick = onDelete)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.close),
                contentDescription = "Icon delete",
                tint = Primary,
                modifier = Modifier
                    .padding(5.dp)
                    .size(10.dp),
            )
        }
    }
}