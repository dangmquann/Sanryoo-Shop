package com.sanryoo.shopping.feature.presentation._component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.SnackbarData
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SnackBar(
    modifier: Modifier = Modifier,
    snackBarData: SnackbarData
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 30.dp),
        color = Color(0xFF222222),
        contentColor = Color.White,
        shape = RoundedCornerShape(15.dp)
    ) {
        Text(
            text = snackBarData.message,
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            style = TextStyle(textAlign = TextAlign.Center),
            fontSize = 18.sp
        )
    }
}