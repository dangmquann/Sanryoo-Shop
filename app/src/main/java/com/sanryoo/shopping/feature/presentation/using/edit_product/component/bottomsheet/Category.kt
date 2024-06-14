package com.sanryoo.shopping.feature.presentation.using.edit_product.component.bottomsheet

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanryoo.shopping.R
import com.sanryoo.shopping.feature.domain.model.Category
import com.sanryoo.shopping.ui.theme.Primary

@ExperimentalAnimationApi
@Composable
fun Category(
    categories: List<Category> = emptyList(),
    currentCategory: Category = Category(),
    category: List<String> = emptyList(),
    setCategory: (List<String>) -> Unit = {},
    hideBottomSheet: () -> Unit = {}
) {
    var selectedIndex by remember {
        mutableStateOf(0)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 10.dp)
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
                modifier = Modifier.align(Alignment.Center),
                text = "Choose Category",
                fontSize = 26.sp,
            )
        }
        Divider(modifier = Modifier.fillMaxWidth())
        TabRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            selectedTabIndex = selectedIndex,
            backgroundColor = MaterialTheme.colors.background,
            contentColor = Primary,
        ) {
            Tab(
                selected = selectedIndex == 0,
                onClick = { selectedIndex = 0 },
                selectedContentColor = Primary,
                unselectedContentColor = MaterialTheme.colors.onBackground,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = if (category.isEmpty()) "Please Choose" else category[0]
                )
            }
            if (category.isNotEmpty()) {
                Tab(
                    selected = selectedIndex == 1,
                    onClick = { selectedIndex = 1 },
                    selectedContentColor = Primary,
                    unselectedContentColor = MaterialTheme.colors.onBackground,
                    modifier = Modifier
                        .fillMaxHeight()
                ) {
                    Text(
                        text = if (category.size <= 1) "Please Choose" else category[1]
                    )
                }
            } else {
                Spacer(modifier = Modifier.fillMaxWidth(0.5f))
            }
        }
        AnimatedContent(
            targetState = selectedIndex,
            transitionSpec = {
                slideInHorizontally(
                    animationSpec = tween(200),
                    initialOffsetX = { if(selectedIndex == 0) -it else it }
                ).with(slideOutHorizontally(
                    animationSpec = tween(200),
                    targetOffsetX = { if(selectedIndex == 0) it else -it }
                ))
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            content = { index ->
                if (index == 0) {
                    LazyColumn {
                        items(categories) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        setCategory(listOf(it.name))
                                        selectedIndex = 1
                                    }
                            ) {
                                Text(
                                    text = it.name,
                                    modifier = Modifier.padding(15.dp),
                                    color = if (category.contains(it.name)) Primary else MaterialTheme.colors.onBackground
                                )
                            }
                            Divider(modifier = Modifier.fillMaxWidth())
                        }

                    }
                } else {
                    LazyColumn {
                        items(currentCategory.child) { childName ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        setCategory(category + childName)
                                        hideBottomSheet()
                                    }
                            ) {
                                Text(
                                    text = childName,
                                    modifier = Modifier.padding(15.dp),
                                    color = if (category.contains(childName)) Primary else MaterialTheme.colors.onBackground
                                )
                            }
                            Divider(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        )
    }
}