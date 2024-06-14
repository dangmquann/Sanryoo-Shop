package com.sanryoo.shopping.feature.presentation.using.edit_product.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanryoo.shopping.R
import com.sanryoo.shopping.feature.util.decimalFormat
import com.sanryoo.shopping.ui.theme.Primary

@Composable
fun ItemProductPrice(
    price: Long = 0,
    onChangePrice: (Long) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.price),
            contentDescription = "Icon price",
            modifier = Modifier
                .padding(15.dp)
                .size(25.dp)
        )
        Text(text = "Price: ", modifier = Modifier.padding(vertical = 15.dp))
        Box(modifier = Modifier.weight(1f)) {
            BasicTextField(
                value = if (price > 0) {
                    TextFieldValue(
                        text = price.toString(),
                        selection = TextRange(price.toString().length)
                    )
                } else {
                    TextFieldValue()
                },
                onValueChange = {
                    try {
                        if (it.text.isBlank()) {
                            onChangePrice(0)
                        } else if (it.text.length <= 12 && it.text.toLong() > 0) {
                            onChangePrice(it.text.toLong())
                        }
                    } catch (_: Exception) {}
                },
                textStyle = TextStyle(
                    textAlign = TextAlign.End,
                    color = Color.Transparent,
                    fontSize = 18.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                cursorBrush = SolidColor(Primary),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                text = try {
                    if (price > 0) {
                        decimalFormat.format(price)
                    } else {
                        "Set"
                    }
                } catch (_: Exception) {
                    "Set"
                },
                fontSize = 18.sp,
                textAlign = TextAlign.End,
                color = if (price > 0)
                    MaterialTheme.colors.onBackground
                else
                    MaterialTheme.colors.onBackground.copy(alpha = 0.5f),
            )
        }
        Text(
            text = "đ",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 15.dp, bottom = 15.dp, end = 15.dp),
        )
    }
}