package com.sanryoo.shopping.feature.presentation.using.my_account.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanryoo.shopping.ui.theme.Primary

@ExperimentalComposeUiApi
@Composable
fun ChangeInformationDialog(
    visible: Boolean = false,
    label: String = "",
    originalContent: String = "",
    maxLines: Int = 1,
    maxLengthContent: Int = 30,
    onChange: (String) -> Unit = {},
    onDismiss: (Boolean) -> Unit = {},
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = { onDismiss(false) },
            shape = RoundedCornerShape(20.dp),
            buttons = {
                val focusManager = LocalFocusManager.current
                val keyboardController = LocalSoftwareKeyboardController.current
                var content by remember {
                    mutableStateOf(
                        TextFieldValue(
                            text = originalContent,
                            selection = TextRange(originalContent.length)
                        )
                    )
                }
                val focusRequester = remember {
                    FocusRequester()
                }
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .width(400.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = label, fontSize = 26.sp, color = Primary)
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedTextField(
                        value = content,
                        onValueChange = {
                            if (it.text.length < maxLengthContent) {
                                content = it
                            }
                        },
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .background(MaterialTheme.colors.background, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }),
                        maxLines = maxLines
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { onDismiss(false) },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "Cancel")
                        }
                        Button(
                            onClick = {
                                onChange(content.text)
                                onDismiss(false)
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "Change")
                        }
                    }
                }
            }
        )
    }
}
