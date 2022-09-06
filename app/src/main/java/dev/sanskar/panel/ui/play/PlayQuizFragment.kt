package dev.sanskar.panel.ui.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import dev.sanskar.panel.ui.components.ErrorDialog
import dev.sanskar.panel.ui.data.Question
import dev.sanskar.panel.util.UiState
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
        when(val state = viewModel.quizQuestions) {
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
                Questions(state.data)
            }
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun Questions(questions: List<Question>, modifier: Modifier = Modifier) {
        Column(modifier) {
            val pagerState = rememberPagerState()
            val scope = rememberCoroutineScope()
            HorizontalPager(
                count = questions.size,
                state = pagerState,
            ) { questionNumber ->
                Question(questions[questionNumber])
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
    fun Question(question: Question, modifier: Modifier = Modifier) {
        Column {
            Text(question.questionText)
        }
    }
}