package dev.sanskar.panel.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction

@Composable
fun StatefulPanelTextField(modifier: Modifier = Modifier, onDone: (String) -> Unit) {
    var value by remember { mutableStateOf("") }
    var startedTyping by remember { mutableStateOf(false) }
    val errorState by remember { derivedStateOf {
        startedTyping && value.isEmpty()
    } }
    PanelTextField(
        state = value,
        modifier = modifier,
        isError = errorState,
        onChanged = {
            startedTyping = true
            value = it
        },
        onDone = {
            startedTyping = false
            onDone(value)
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PanelTextField(
    state: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    label: String = "Answer",
    placeholder: String = "Type your answer here",
    icon: ImageVector = Icons.Default.QuestionAnswer,
    onChanged: (String) -> Unit,
    onDone: (String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(),
        value = state,
        onValueChange = {
            onChanged(it)
        },
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
        isError = isError,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                if (state.isNotEmpty()) {
                    onDone(state)
                    keyboardController?.hide()
                }
            }
        ),
        maxLines = 10,
    )
}