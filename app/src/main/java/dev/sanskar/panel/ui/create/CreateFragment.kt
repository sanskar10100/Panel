package dev.sanskar.panel.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dev.sanskar.panel.ui.components.AnswerField
import dev.sanskar.panel.ui.components.BinaryAnswer
import dev.sanskar.panel.ui.components.MultipleChoiceAnswer
import dev.sanskar.panel.ui.components.MultipleSelectAnswer
import dev.sanskar.panel.ui.theme.PanelTheme

class CreateFragment : Fragment() {
    private val viewModel by viewModels<CreateViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PanelTheme {
                    CreateScreen()
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun CreateScreen() {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            stickyHeader {
                Spacer(Modifier.height(32.dp))
                QuestionField(onDone = {
                    viewModel.question = it
                })
                Spacer(Modifier.height(32.dp))
            }
            AnswerTypeSelector()
        }
    }

    @Composable
    fun QuestionField(onDone: (String) -> Unit, modifier: Modifier = Modifier) {
        var value by remember { mutableStateOf("") }
        var startedTyping by remember { mutableStateOf(false) }
        val errorState by derivedStateOf {
            startedTyping && value.isEmpty()
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(0.9f),
            value = value,
            onValueChange = {
                value = it
                startedTyping = true
            },
            label = { Text("Question")},
            placeholder = { Text("What's the value of g?") },
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

    private fun LazyListScope.AnswerTypeSelector() {
        item {
            AnimatedVisibility(
                viewModel.showSelector,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .border(1.dp, Color.Blue, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                BinaryAnswer(
                    modifier = Modifier.clickable(false) {}
                ){

                }
                Spacer(Modifier.height(8.dp))
            }
        }

        item {
            Spacer(Modifier.height(32.dp))
            AnimatedVisibility(
                viewModel.showSelector,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .border(1.dp, Color.Blue, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                MultipleChoiceAnswer(
                    options = listOf("Option 1", "Option 2", "Option 3"),
                    onSelected = { },
                    modifier = Modifier.padding(horizontal = 32.dp).clickable(false) {}
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        item {
            Spacer(Modifier.height(32.dp))
            AnimatedVisibility(
                viewModel.showSelector,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .border(1.dp, Color.Blue, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                MultipleSelectAnswer(
                    options = listOf("Option 1", "Option 2", "Option 3"),
                    onSelected = { },
                    modifier = Modifier.padding(horizontal = 32.dp).clickable(false) {}
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        item {
            Spacer(Modifier.height(32.dp))
            AnimatedVisibility(
                viewModel.showSelector,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .border(1.dp, Color.Blue, shape = RoundedCornerShape(8.dp))
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            ) {
                AnswerField(
                    modifier = Modifier.clickable(false) {}
                ) {}
            }
        }
    }
}