package com.sanryoo.shopping.feature.presentation.using.home.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@ExperimentalMaterialApi
@Composable
fun ChooseCategory(
    categories: List<String> = emptyList(),
    setCurrentCategory: (String) -> Unit = {}
) {
    val listState = rememberLazyListState()
    var selectedIndex by remember {
        mutableStateOf(0)
    }
    LaunchedEffect(selectedIndex) {
        listState.animateScrollToItem(
            index = selectedIndex + 1,
            scrollOffset = -300
        )
    }
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        state = listState
    ) {
        item {
            Spacer(modifier = Modifier.width(10.dp))
        }
        items(categories.size) { index ->
            val isSelected = selectedIndex == index
            val backgroundColor = animateColorAsState(
                targetValue = if (isSelected)
                    MaterialTheme.colors.onBackground
                else
                    MaterialTheme.colors.surface,
                animationSpec = tween(200)
            )
            Card(
                onClick = {
                    selectedIndex = index
                    setCurrentCategory(categories[index])
                },
                backgroundColor = backgroundColor.value,
                contentColor = if (isSelected) MaterialTheme.colors.background else MaterialTheme.colors.onBackground,
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = categories[index],
                    modifier = Modifier.padding(
                        horizontal = 10.dp, vertical = 5.dp
                    )
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
        }
    }
}