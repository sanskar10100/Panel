package dev.sanskar.panel.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sanskar.panel.util.clickWithRipple

@Composable
fun BinaryAnswer(modifier: Modifier = Modifier, builderMode: AnswerBuilder = AnswerBuilder(), onSelected: (Boolean) -> Unit) {
    FullWidthColumnWithCenteredChildren {
        Row(
            modifier = modifier
                .height(48.dp)
                .border(3.dp, color = Color.Blue, RoundedCornerShape(8.dp))
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickWithRipple {
                        onSelected(true)
                    },
            ) {
                Text(
                    "True",
                    modifier = Modifier
                        .align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
            Divider(
                color = Color.Black,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickWithRipple {
                        onSelected(true)
                    },
            ) {
                Text(
                    "False",
                    modifier = Modifier
                        .align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
        }
        if (builderMode.enabled) {
            Spacer(Modifier.height(64.dp))
            Text(
                "Select one of the possible options as the correct answer to continue",
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MultipleChoiceAnswer(
    options: List<String>,
    modifier: Modifier = Modifier,
    builderMode: AnswerBuilder = AnswerBuilder(),
    onSelected: (String) -> Unit) {
    var selected by remember { mutableStateOf("") }
    val optionsState = remember { options.toMutableStateList() }
    val select = { option: String ->
        selected = option
        if (builderMode.enabled) {
            builderMode.options = optionsState.toList()
            builderMode.selected = listOf(selected)
        }
        onSelected(option)
    }

    Column(
        modifier = modifier
    ) {
        optionsState.forEach { option ->
            Row(
                modifier = Modifier.clickWithRipple { select(option) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = option)
                Spacer(Modifier.weight(1f))
                RadioButton(selected = selected == option, onClick = { select(option) })
            }
        }
        if (builderMode.enabled) {
            var value by remember { mutableStateOf("") }
            var startedTyping by remember { mutableStateOf(false) }
            val errorState by derivedStateOf {
                startedTyping && value.isEmpty()
            }
            PanelTextField(
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
fun MultipleSelectAnswer(options: List<String>, modifier: Modifier = Modifier, builderMode: AnswerBuilder = AnswerBuilder(), onSelected: (List<String>) -> Unit) {
    val optionsState = remember { options.toMutableStateList() }
    val selected = remember { mutableStateListOf<String>() }
    val select = { option: String ->
        if (option !in selected) selected.add(option) else selected.remove(option)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        optionsState.forEach { option ->
            Row(
                modifier = Modifier
                    .clickWithRipple { select(option) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = option)
                Spacer(Modifier.weight(1f))
                Checkbox(
                    checked = option in selected,
                    onCheckedChange = { select(option) }
                )
            }
        }
        if (builderMode.enabled) {
            var value by remember { mutableStateOf("") }
            var startedTyping by remember { mutableStateOf(false) }
            val errorState by derivedStateOf {
                startedTyping && value.isEmpty()
            }
            PanelTextField(
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

data class AnswerBuilder(
    val enabled: Boolean = false,
    var options: List<String> = mutableListOf(),
    var selected: List<String> = mutableListOf(),
)