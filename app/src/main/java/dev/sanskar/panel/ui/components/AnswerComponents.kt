package dev.sanskar.panel.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierInfo
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sanskar.panel.util.clickWithRipple

@Composable
fun BinaryAnswer(modifier: Modifier = Modifier, onSelected: (Boolean) -> Unit) {
    BoxWithConstraints(
        modifier = modifier
            .height(48.dp)
            .border(3.dp, color = Color.Blue, RoundedCornerShape(8.dp))
            .padding(4.dp),
    ) {
        Text(
            "True",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp)
                .clickWithRipple {
                    onSelected(true)
                }
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(1.dp)
                .height(maxHeight)
                .background(Color.Red)
        )
        Text(
            "False",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
                .clickWithRipple {
                    onSelected(false)
                }
        )
    }
}

@Composable
fun MultipleChoiceAnswer(
    options: List<String>,
    modifier: Modifier = Modifier,
    builderMode: MultipleAnswerBuilder = MultipleAnswerBuilder(),
    onSelected: (String) -> Unit) {
    var selected by remember { mutableStateOf("") }
    val optionsState = remember { options.toMutableStateList() }
    Column(
        modifier = modifier
    ) {
        optionsState.forEach { option ->
            Row(
                modifier = Modifier.clickWithRipple { onSelected(option) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = option)
                Spacer(Modifier.weight(1f))
                RadioButton(selected = selected == option, onClick = {
                    selected = option
                    if (builderMode.enabled) {
                        builderMode.options = optionsState.toList()
                        builderMode.selected = listOf(selected)
                    }
                    onSelected(option)
                })
            }
        }
        if (builderMode.enabled) {
            var value by remember { mutableStateOf("") }
            var startedTyping by remember { mutableStateOf(false) }
            val errorState by derivedStateOf {
                startedTyping && value.isEmpty()
            }
            AnswerField(
                state = value,
                modifier = Modifier.fillMaxWidth(),
                isError = errorState,
                onChanged = {
                    startedTyping = true
                    value = it
                },
                onDone = {
                    startedTyping = false
                    if (it !in optionsState) optionsState.add(it)
                    value = ""
                }
            )

            Spacer(Modifier.height(64.dp))
            Text(
                "Add options using the field above, then select the correct option to continue",
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MultipleSelectAnswer(options: List<String>, modifier: Modifier = Modifier, builderMode: MultipleAnswerBuilder = MultipleAnswerBuilder(), onSelected: (List<String>) -> Unit) {
    val optionsState = remember { options.toMutableStateList() }
    val selected = remember { mutableStateListOf<String>() }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        optionsState.forEach { option ->
            Row(
                modifier = Modifier
                    .clickWithRipple { if (option !in selected) selected.add(option) else selected.remove(option) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = option)
                Spacer(Modifier.weight(1f))
                Checkbox(
                    checked = option in selected,
                    onCheckedChange = {
                        if (option !in selected) selected.add(option) else selected.remove(option)
                    }
                )
            }
        }
        if (builderMode.enabled) {
            var value by remember { mutableStateOf("") }
            var startedTyping by remember { mutableStateOf(false) }
            val errorState by derivedStateOf {
                startedTyping && value.isEmpty()
            }
            AnswerField(
                state = value,
                modifier = Modifier.fillMaxWidth(),
                isError = errorState,
                onChanged = {
                    startedTyping = true
                    value = it
                },
                onDone = {
                    startedTyping = false
                    if (it !in optionsState) optionsState.add(it)
                    value = ""
                }
            )
            Spacer(Modifier.height(16.dp))
        }
        Button(
            onClick = {
                if (optionsState.isNotEmpty() and selected.isNotEmpty()) {
                    if (builderMode.enabled) {
                        builderMode.options = optionsState.toList()
                        builderMode.selected = selected.toList()
                    }
                    onSelected(selected.toList())
                }
            }
        ) {
            Text("Submit")
        }
        if (builderMode.enabled) {
            Spacer(Modifier.height(64.dp))
            Text(
                "Add options using the field above, select correct options, then press submit to continue",
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StatefulAnswerField(modifier: Modifier = Modifier, onDone: (String) -> Unit) {
    var value by remember { mutableStateOf("") }
    var startedTyping by remember { mutableStateOf(false) }
    val errorState by derivedStateOf {
        startedTyping && value.isEmpty()
    }
    AnswerField(
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

@Composable
fun AnswerField(state: String, modifier: Modifier = Modifier, isError: Boolean = false, onChanged: (String) -> Unit, onDone: (String) -> Unit, ) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(0.9f),
        value = state,
        onValueChange = {
            onChanged(it)
        },
        label = { Text("Answer")},
        placeholder = { Text("9.8") },
        leadingIcon = { Icon(imageVector = Icons.Default.QuestionAnswer, contentDescription = null) },
        isError = isError,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                if (state.isNotEmpty()) onDone(state)
            }
        ),
        maxLines = 10,
    )
}

data class MultipleAnswerBuilder(
    val enabled: Boolean = false,
    var options: List<String> = mutableListOf(),
    var selected: List<String> = mutableListOf(),
)