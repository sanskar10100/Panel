package dev.sanskar.panel.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dev.sanskar.panel.ui.components.BinaryAnswer
import dev.sanskar.panel.ui.components.MultipleAnswerBuilder
import dev.sanskar.panel.ui.components.MultipleChoiceAnswer
import dev.sanskar.panel.ui.components.MultipleSelectAnswer
import dev.sanskar.panel.ui.components.PanelTextField
import dev.sanskar.panel.ui.components.StatefulPanelTextField
import dev.sanskar.panel.ui.data.AnswerType
import dev.sanskar.panel.ui.theme.PanelTheme
import dev.sanskar.panel.util.clickWithRipple

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
                Column(
                    modifier = Modifier.background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(32.dp))
                    PanelTextField(
                        state = viewModel.questionText,
                        modifier = Modifier.fillMaxWidth(0.9f),
                        isError = false,
                        label = "Question",
                        placeholder = "What is the value of g?",
                        icon = Icons.Default.QuestionMark,
                        onChanged = { viewModel.questionText = it },
                        onDone = { viewModel.questionText = it }
                    )
                    Spacer(Modifier.height(32.dp))
                }
            }
            val mcqBuilder = MultipleAnswerBuilder(true)
            val msqBuilder = MultipleAnswerBuilder(true)
            when (viewModel.answerType) {
                AnswerType.NONE -> answerTypeSelector()
                AnswerType.BINARY -> item {
                    BinaryAnswer(modifier = Modifier.fillMaxWidth(0.7f),
                        onSelected = { viewModel.addBinaryQuestion(it) })
                }
                AnswerType.MCQ -> item {
                    MultipleChoiceAnswer(emptyList(),
                        Modifier.fillMaxWidth(0.9f),
                        builderMode = mcqBuilder) { viewModel.addMultipleChoiceQuestion(mcqBuilder) }
                }
                AnswerType.MSQ -> item {
                    MultipleSelectAnswer(emptyList(),
                        Modifier.fillMaxWidth(0.9f),
                        builderMode = msqBuilder) { viewModel.addMultipleSelectQuestion(msqBuilder) }
                }
                AnswerType.TEXT -> item { StatefulPanelTextField { viewModel.addTextQuestion(it) } }
            }
        }
    }

    fun display(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun LazyListScope.answerTypeSelector() {
        item {
            AnimatedVisibility(viewModel.showSelector.value) {
                Text(
                    "Please select an answer type",
                    style = MaterialTheme.typography.h6
                )
                Spacer(Modifier.height(64.dp))
            }
        }

        item {
            AnimatedVisibility(
                viewModel.showSelector.value,
                modifier = Modifier
                    .clickWithRipple { viewModel.selectAnswerType(AnswerType.BINARY) }
                    .fillMaxWidth(0.7f)
                    .border(1.dp, Color.Blue, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                BinaryAnswer(
                    modifier = Modifier.clickable(false) {}
                ) {

                }
                Spacer(Modifier.height(8.dp))
            }
        }

        item {
            Spacer(Modifier.height(32.dp))
            AnimatedVisibility(
                viewModel.showSelector.value,
                modifier = Modifier
                    .clickWithRipple { viewModel.selectAnswerType(AnswerType.MCQ) }
                    .fillMaxWidth(0.9f)
                    .border(1.dp, Color.Blue, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                MultipleChoiceAnswer(
                    options = listOf("Option 1", "Option 2", "Option 3"),
                    onSelected = { },
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .clickable(false) {}
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        item {
            Spacer(Modifier.height(32.dp))
            AnimatedVisibility(
                viewModel.showSelector.value,
                modifier = Modifier
                    .clickWithRipple { viewModel.selectAnswerType(AnswerType.MSQ) }
                    .fillMaxWidth(0.9f)
                    .border(1.dp, Color.Blue, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                MultipleSelectAnswer(
                    options = listOf("Option 1", "Option 2", "Option 3"),
                    onSelected = { },
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .clickable(false) {}
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        item {
            Spacer(Modifier.height(32.dp))
            AnimatedVisibility(
                viewModel.showSelector.value,
                modifier = Modifier
                    .clickWithRipple { viewModel.selectAnswerType(AnswerType.TEXT) }
                    .fillMaxWidth(0.9f)
                    .border(1.dp, Color.Blue, shape = RoundedCornerShape(8.dp))
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            ) {
                StatefulPanelTextField(
                    modifier = Modifier.clickable(false) {}
                ) {}
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}