package dev.sanskar.panel.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import dev.sanskar.panel.util.clickWithRipple

enum class BinaryAnswerChoice {
    TRUE,
    FALSE
}

@Composable
fun BinaryAnswer(modifier: Modifier = Modifier, onSelected: (BinaryAnswerChoice) -> Unit) {
    BoxWithConstraints(
        modifier = modifier
            .height(48.dp)
            .width(72.dp)
            .border(3.dp, color = Color.Blue, RoundedCornerShape(8.dp))
            .padding(4.dp),
    ) {
        Text(
            "True",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp)
                .clickWithRipple {
                    onSelected(BinaryAnswerChoice.TRUE)
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
                    onSelected(BinaryAnswerChoice.FALSE)
                }
        )
    }
}

@Composable
fun MultipleChoiceAnswer(options: List<String>, modifier: Modifier = Modifier, onSelected: (String) -> Unit) {
    var selected by remember { mutableStateOf("") }
    Column(
        modifier = modifier
    ) {
        options.forEach { option ->
            Row(
                modifier = Modifier.clickWithRipple { onSelected(option) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = option)
                Spacer(Modifier.weight(1f))
                RadioButton(selected = selected == option, onClick = {
                    selected = option
                    onSelected(option)
                })
            }
        }
    }
}

@Composable
fun MultipleSelectAnswer(options: List<String>, modifier: Modifier = Modifier, onSelected: (List<String>) -> Unit) {
    val selected = remember { mutableStateListOf<String>() }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        options.forEach { option ->
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
        Button(
            onClick = { onSelected(selected.toList()) },
        ) {
            Text("Submit")
        }
    }
}

@Composable
fun AnswerField(modifier: Modifier = Modifier, onDone: (String) -> Unit, ) {
    var value by remember { mutableStateOf("") }
    var startedTyping by remember { mutableStateOf(false) }
    val errorState by derivedStateOf {
        startedTyping && value.isEmpty()
    }

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(0.9f),
        value = value,
        onValueChange = {
            value = it
            startedTyping = true
        },
        label = { Text("Answer")},
        placeholder = { Text("9.8") },
        leadingIcon = { Icon(imageVector = Icons.Default.QuestionAnswer, contentDescription = null) },
        isError = errorState,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { if (!errorState) onDone(value) }
        ),
        maxLines = 10,
    )
}