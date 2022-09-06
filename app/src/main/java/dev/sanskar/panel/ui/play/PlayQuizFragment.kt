package dev.sanskar.panel.ui.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import dev.sanskar.panel.ui.components.BinaryAnswer
import dev.sanskar.panel.ui.components.ErrorDialog
import dev.sanskar.panel.ui.components.FullWidthColumnWithCenteredChildren
import dev.sanskar.panel.ui.components.InterceptClickBox
import dev.sanskar.panel.ui.components.MultipleChoiceAnswer
import dev.sanskar.panel.ui.components.MultipleSelectAnswer
import dev.sanskar.panel.ui.components.StatefulPanelTextField
import dev.sanskar.panel.ui.data.AnswerState
import dev.sanskar.panel.ui.data.AnswerType
import dev.sanskar.panel.ui.data.Question
import dev.sanskar.panel.ui.theme.PanelTheme
import dev.sanskar.panel.util.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayQuizFragment : Fragment() {
    private val viewModel by viewModels<PlayViewModel>()
    private val args by navArgs<PlayQuizFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.loadQuiz(args.code)

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PlayQuizScreen()
            }
        }
    }

    @Composable
    fun PlayQuizScreen(modifier: Modifier = Modifier) {
        when(val state = viewModel.quizLoadState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize(0.5f)
                            .align(Alignment.Center)
                    )
                }
            }
            is UiState.Error -> {
                ErrorDialog(message = state.message) { findNavController().navigateUp() }
            }
            is UiState.Success -> {
                Questions(viewModel.quizQuestions)
            }
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun Questions(questions: SnapshotStateList<Question>, modifier: Modifier = Modifier) {

        Column(modifier) {
            val pagerState = rememberPagerState()
            val scope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                viewModel.nextPage.collect {
                    if (it) {
                        if (pagerState.currentPage < questions.size - 1) {
                            delay(500)
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            delay(1000)
                            findNavController().navigateUp()
                        }
                    }
                }
            }

            HorizontalPager(
                count = questions.size,
                state = pagerState,
                modifier = Modifier.fillMaxHeight(0.6f)
            ) { questionNumber ->
                FullWidthColumnWithCenteredChildren {
                    QuestionUi(
                        question = questions[questionNumber],
                        enabled = questions[questionNumber].answerState == AnswerState.Unanswered,
                    )

                    if (questions[questionNumber].answerState != AnswerState.Unanswered) {
                        if (questions[questionNumber].answerState == AnswerState.Correct) {
                            Text("Correct!")
                        } else {
                            Text("Incorrect!")
                        }
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    pagerState.currentPage > 0,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                    ) {
                        Text("Previous")
                    }
                }

                androidx.compose.animation.AnimatedVisibility(
                    pagerState.currentPage < questions.size - 1,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                    ) {
                        Text("Next")
                    }
                }
            }
        }
    }

    @Composable
    fun QuestionUi(question: Question, modifier: Modifier = Modifier, enabled: Boolean) {
        Surface(
            elevation = 5.dp,
            shape = RoundedCornerShape(8.dp),
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            FullWidthColumnWithCenteredChildren(
                Modifier.padding(16.dp)
            ) {
                Text(
                    question.questionText,
                    style = MaterialTheme.typography.h6
                )
                Spacer(Modifier.height(32.dp))
                when (question.type) {
                    AnswerType.NONE -> {}
                    AnswerType.BINARY -> {
                        InterceptClickBox(childClickEnabled = enabled, ) {
                            BinaryAnswer {
                                viewModel.checkBinaryAnswer(question, it)
                            }
                        }
                    }
                    AnswerType.MCQ -> {
                        InterceptClickBox(childClickEnabled = enabled) {
                            MultipleChoiceAnswer(question.options) {
                                viewModel.checkMultipleChoiceAnswer(question, it)
                            }
                        }
                    }
                    AnswerType.MSQ -> {
                        InterceptClickBox(childClickEnabled = enabled) {
                            MultipleSelectAnswer(question.options) {
                                viewModel.checkMultipleSelectAnswer(question, it)
                            }
                        }
                    }
                    AnswerType.TEXT -> {
                        InterceptClickBox(childClickEnabled = enabled) {
                            StatefulPanelTextField {
                                viewModel.checkTextAnswer(question, it)
                            }
                        }
                    }
                }
            }
        }
    }

    @Preview(widthDp = 320, showBackground = true)
    @Composable
    fun QuestionPreview() {
        PanelTheme {
            QuestionUi(
                Question(
                    questionText = "Who presented the theory of General Relativity",
                    options = listOf("Max Planck", "Albert Einstein", "Niels Bohr", "Werner Heisenberg"),
                    correct = "Albert Einstein",
                    type = AnswerType.MCQ
                ),
                enabled = true,
            )
        }
    }
}