package dev.sanskar.panel.ui.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dev.sanskar.panel.ui.components.ErrorDialog
import dev.sanskar.panel.util.UiState

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
                Text(
                    modifier = Modifier.fillMaxSize(),
                    text = state.data.toString()
                )
            }
        }
    }
}