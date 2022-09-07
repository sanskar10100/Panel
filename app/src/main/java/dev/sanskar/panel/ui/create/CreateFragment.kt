package dev.sanskar.panel.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropValue
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import dev.sanskar.panel.R
import dev.sanskar.panel.ui.components.AnswerBuilder
import dev.sanskar.panel.ui.components.BinaryAnswer
import dev.sanskar.panel.ui.components.FullWidthColumnWithCenteredChildren
import dev.sanskar.panel.ui.components.InterceptClickBox
import dev.sanskar.panel.ui.components.MultipleChoiceAnswer
import dev.sanskar.panel.ui.components.MultipleSelectAnswer
import dev.sanskar.panel.ui.components.PanelTextField
import dev.sanskar.panel.ui.components.StatefulPanelTextField
import dev.sanskar.panel.ui.data.AnswerType
import dev.sanskar.panel.ui.data.getMultipleCorrectAnswers
import dev.sanskar.panel.ui.theme.PanelTheme
import kotlinx.coroutines.launch

class CreateFragment : Fragment() {
    private val viewModel by navGraphViewModels<CreateViewModel>(R.id.graph_create)

    @OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PanelTheme {
                    val scaffoldState = rememberScaffoldState()
                    val backdropScaffoldState =
                        rememberBackdropScaffoldState(BackdropValue.Revealed)
                    val scope = rememberCoroutineScope()
                    val keyboardController = LocalSoftwareKeyboardController.current
                    Scaffold(
                        scaffoldState = scaffoldState,
                        drawerElevation = 5.dp,
                        drawerShape = RoundedCornerShape(8.dp),
                        drawerContent = {
                            CurrentQuestionsList()
                        }
                    ) {
                        LaunchedEffect(viewModel.addQuestionSnackbar) {
                            if (viewModel.addQuestionSnackbar.isNotEmpty()) {
                                backdropScaffoldState.reveal()
                                scaffoldState.snackbarHostState.showSnackbar(viewModel.addQuestionSnackbar)
                                viewModel.addQuestionSnackbar = ""
                            }
                        }
                        BackdropScaffold(
                            appBar = { },
                            backLayerContent = {
                                Header {
                                    scope.launch {
                                        backdropScaffoldState.conceal()
                                    }
                                }
                            },
                            frontLayerContent = { AnswerContent() },
                            modifier = Modifier.padding(it),
                            scaffoldState = backdropScaffoldState,
                            backLayerBackgroundColor = Color(0xFFF5F5F5),
                            peekHeight = 64.dp
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun Header(modifier: Modifier = Modifier, onQuestionInput: () -> Unit) {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                "Swipe to right to see existing questions and finalize the quiz",
                style = MaterialTheme.typography.subtitle2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
            Spacer(Modifier.height(32.dp))
            PanelTextField(
                state = viewModel.questionText,
                modifier = Modifier.fillMaxWidth(0.9f),
                isError = false,
                label = "Question",
                placeholder = "What is the value of g?",
                icon = Icons.Default.QuestionMark,
                onChanged = { viewModel.questionText = it },
                onDone = { onQuestionInput() }
            )
            Spacer(Modifier.height(32.dp))
        }
    }

    @Composable
    fun AnswerContent(modifier: Modifier = Modifier) {
        BackHandler(viewModel.answerType != AnswerType.NONE) {
            viewModel.answerType = AnswerType.NONE
        }

        FullWidthColumnWithCenteredChildren {
            when (viewModel.answerType) {
                AnswerType.NONE -> AnswerTypeSelector()
                AnswerType.BINARY -> {
                    BinaryAnswer(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .padding(top = 16.dp),
                        builderMode = AnswerBuilder(true),
                        onSelected = { viewModel.addBinaryQuestion(it) }
                    )
                }
                AnswerType.MCQ -> {
                    val mcqBuilder = AnswerBuilder(true)
                    MultipleChoiceAnswer(emptyList(),
                        Modifier.fillMaxWidth(0.9f),
                        builderMode = mcqBuilder) {
                        viewModel.addMultipleChoiceQuestion(mcqBuilder)
                    }
                }
                AnswerType.MSQ -> {
                    val msqBuilder = AnswerBuilder(true)
                    MultipleSelectAnswer(emptyList(),
                        Modifier.fillMaxWidth(0.9f),
                        builderMode = msqBuilder) {
                        viewModel.addMultipleSelectQuestion(msqBuilder)
                    }
                }
                AnswerType.TEXT -> {
                    StatefulPanelTextField(
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) { viewModel.addTextQuestion(it) }
                }
            }
        }
    }

    @Composable
    private fun AnswerTypeSelector() {

        Spacer(Modifier.height(16.dp))
        AnimatedVisibility(viewModel.showSelector.value) {
            FullWidthColumnWithCenteredChildren(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Please select an answer type",
                    style = MaterialTheme.typography.h6
                )
                Spacer(Modifier.height(32.dp))

                InterceptClickBox(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(IntrinsicSize.Min)
                        .border(1.dp, Color.DarkGray, shape = RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    onClick = { viewModel.answerType = AnswerType.BINARY }
                ) {
                    BinaryAnswer {}
                }

                Spacer(Modifier.height(32.dp))
                InterceptClickBox(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(IntrinsicSize.Min)
                        .border(1.dp, Color.DarkGray, shape = RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    onClick = { viewModel.selectAnswerType(AnswerType.MCQ) }
                ) {
                    MultipleChoiceAnswer(
                        options = listOf("Option 1", "Option 2", "Option 3"),
                        onSelected = { },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    )
                }

                Spacer(Modifier.height(32.dp))
                InterceptClickBox(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(IntrinsicSize.Min)
                        .border(1.dp, Color.DarkGray, shape = RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    onClick = { viewModel.selectAnswerType(AnswerType.MSQ) }
                ) {
                    MultipleSelectAnswer(
                        options = listOf("Option 1", "Option 2", "Option 3"),
                        onSelected = { },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    )
                }

                Spacer(Modifier.height(32.dp))
                InterceptClickBox(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(IntrinsicSize.Min)
                        .border(1.dp, Color.DarkGray, shape = RoundedCornerShape(8.dp))
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                    onClick = { viewModel.selectAnswerType(AnswerType.TEXT) }
                ) {
                    StatefulPanelTextField {}
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun CurrentQuestionsList(modifier: Modifier = Modifier) {
        FullWidthColumnWithCenteredChildren {
            LazyColumn(
                modifier = modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(viewModel.questions) { question ->
                    var expanded by remember { mutableStateOf(false) }
                    val spacerHeight by animateDpAsState(if (expanded) 8.dp else 2.dp)
                    Spacer(Modifier.height(spacerHeight))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp),
                        elevation = 3.dp,
                        shape = RoundedCornerShape(8.dp),
                        onClick = { expanded = !expanded }
                    ) {
                        FullWidthColumnWithCenteredChildren(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(
                                text = question.questionText,
                                fontWeight = FontWeight.Bold,
                            )
                            AnimatedVisibility(visible = expanded) {
                                when (question.type) {
                                    AnswerType.NONE -> {}
                                    AnswerType.BINARY -> Text("Correct Answer: ${question.correct}")
                                    AnswerType.TEXT -> {
                                        OutlinedTextField(
                                            value = question.correct,
                                            onValueChange = { },
                                            readOnly = true
                                        )
                                    }
                                    AnswerType.MCQ -> {
                                        FullWidthColumnWithCenteredChildren {
                                            question.options.forEach {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(it)
                                                    Spacer(Modifier.weight(1f))
                                                    RadioButton(it == question.correct,
                                                        onClick = { })
                                                }
                                            }
                                        }
                                    }
                                    AnswerType.MSQ -> {
                                        val answers = question.correct.getMultipleCorrectAnswers()
                                        FullWidthColumnWithCenteredChildren {
                                            question.options.forEach {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(it)
                                                    Spacer(Modifier.weight(1f))
                                                    Checkbox(it in answers, onCheckedChange = { })
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(spacerHeight))
                }
            }
            Spacer(Modifier.height(8.dp))
            if (viewModel.questions.isNotEmpty()) {
                Button(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    onClick = { findNavController().navigate(R.id.action_createFragment_to_quizGeneratedFragment) }
                ) {
                    Text("Generate Quiz")
                }
            }
        }
    }
}