package com.sanryoo.shopping.feature.presentation.using.edit_product.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanryoo.shopping.ui.theme.Primary

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    label: String = "",
    hint: String = "",
    value: String = "",
    onValueChange: (String) -> Unit = {},
    maxLength: Int = 150,
    imeAction: ImeAction = ImeAction.Done,
) {
    Box(modifier = modifier.background(MaterialTheme.colors.background)) {
        Text(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(15.dp),
            text = label
        )
        Text(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(15.dp),
            text = "${value.length}/$maxLength",
            color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f)
        )
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .padding(top = 30.dp),
            value = value,
            onValueChange = { if (it.length <= maxLength) onValueChange(it) },
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = MaterialTheme.colors.onBackground,
            ),
            keyboardOptions = KeyboardOptions(imeAction = imeAction),
            cursorBrush = SolidColor(Primary),
        )
        if (value.isEmpty()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .padding(top = 30.dp),
                text = hint,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f)
            )
        }
    }
}