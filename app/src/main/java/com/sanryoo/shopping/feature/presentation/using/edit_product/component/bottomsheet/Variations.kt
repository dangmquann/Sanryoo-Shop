package com.sanryoo.shopping.feature.presentation.using.edit_product.component.bottomsheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanryoo.shopping.R
import com.sanryoo.shopping.feature.domain.model.Variation
import com.sanryoo.shopping.ui.theme.Primary

@ExperimentalFoundationApi
@Composable
fun Variation(
    variations: List<Variation> = emptyList(),
    hideBottomSheet: () -> Unit = {},
    onChangeVariation: (List<Variation>) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = { focusManager.clearFocus() }
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(MaterialTheme.colors.background)
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 10.dp)
                    .size(30.dp)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = {
                            focusManager.clearFocus()
                            hideBottomSheet()
                        }
                    ),
                painter = painterResource(id = R.drawable.close),
                contentDescription = "Icon close"
            )
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Variations",
                fontSize = 26.sp,
            )
        }
        Divider(modifier = Modifier.fillMaxWidth())
        variations.forEachIndexed { index, variation ->
            VariationItem(
                variation = variation,
                onVariationChange = {
                    val tempList = variations.toMutableList()
                    tempList[index] = it
                    onChangeVariation(tempList)
                },
                onDeleteVariation = {
                    val tempList = variations.toMutableList()
                    tempList.removeAt(index)
                    onChangeVariation(tempList)
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        if (variations.size < 2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.background)
                    .clickable {
                        onChangeVariation(variations + Variation())
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_variation),
                    contentDescription = "Icon add",
                    modifier = Modifier
                        .padding(15.dp)
                        .size(25.dp)
                )
                Text(text = "Add Variation")
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun VariationItem(
    variation: Variation = Variation(),
    onVariationChange: (Variation) -> Unit = {},
    onDeleteVariation: () -> Unit = {}
) {
    var editing by remember {
        mutableStateOf(true)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                BasicTextField(
                    value = variation.name,
                    onValueChange = {
                        if (it.length <= 30) {
                            onVariationChange(variation.copy(name = it))
                        }
                    },
                    modifier = Modifier
                        .padding(15.dp)
                        .fillMaxWidth(),
                    singleLine = true,
                    enabled = editing
                )
                if (variation.name.isEmpty()) {
                    Text(
                        text = "Variation Name",
                        color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f),
                        modifier = Modifier.padding(15.dp)
                    )
                }
            }
            if (editing) {
                Text(
                    text = "Delete", color = Primary, modifier = Modifier
                        .padding(15.dp)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = onDeleteVariation
                        )
                )
            }
            Text(
                text = if (editing) "Done" else "Edit",
                color = Primary,
                modifier = Modifier
                    .padding(15.dp)
                    .clickable(interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = { editing = !editing }
                    )
            )
        }
        Divider(modifier = Modifier.fillMaxWidth())
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(3),
            content = {
                itemsIndexed(variation.child) { index, value ->
                    Box(
                        modifier = Modifier.padding(10.dp), contentAlignment = Alignment.Center
                    ) {
                        BasicTextField(
                            value = value,
                            onValueChange = {
                                if (it.length <= 20) {
                                    val tempList = variation.child.toMutableList()
                                    tempList[index] = it
                                    onVariationChange(variation.copy(child = tempList))
                                }
                            },
                            modifier = Modifier
                                .padding(5.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colors.onBackground,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(8.dp),
                            enabled = editing,
                            textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 14.sp),
                            singleLine = true
                        )
                        if (editing) {
                            Box(modifier = Modifier
                                .align(Alignment.TopEnd)
                                .clip(CircleShape)
                                .border(
                                    width = 1.dp, color = Primary, shape = CircleShape
                                )
                                .background(MaterialTheme.colors.background)
                                .clickable {
                                    val tempChild = variation.child.toMutableList()
                                    tempChild.removeAt(index)
                                    onVariationChange(variation.copy(child = tempChild))
                                }
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
                }
                if (editing) {
                    item {
                        Box(
                            modifier = Modifier
                                .padding(18.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .border(
                                    width = 1.dp, color = Primary, shape = RoundedCornerShape(4.dp)
                                )
                                .clickable {
                                    onVariationChange(variation.copy(child = variation.child + ""))
                                }, contentAlignment = Alignment.Center
                        ) {
                            Text(text = "+ Add", modifier = Modifier.padding(5.dp), color = Primary)
                        }
                    }
                }
            }
        )
    }
}