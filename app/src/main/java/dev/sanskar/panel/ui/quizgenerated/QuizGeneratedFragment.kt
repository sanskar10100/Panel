package dev.sanskar.panel.ui.quizgenerated

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import dev.sanskar.panel.R
import dev.sanskar.panel.ui.components.FullWidthColumnWithCenteredChildren
import dev.sanskar.panel.ui.components.RetryDialog
import dev.sanskar.panel.ui.create.CreateViewModel
import dev.sanskar.panel.util.UiState

class QuizGeneratedFragment : Fragment() {
    private val viewModel by navGraphViewModels<CreateViewModel>(R.id.graph_create)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.pushQuizToFirebase()

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner))
            setContent {
                QuizGeneratedScreen()
            }
        }
    }

    @Composable
    fun QuizGeneratedScreen() {
        when (val state = viewModel.quizPushId) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize(0.75f)
                    )
                }
            }
            is UiState.Error -> {
                RetryDialog(
                    message = state.message,
                    onDismiss = { findNavController().popBackStack() },
                    onRetry = { viewModel.pushQuizToFirebase() }
                )
            }
            is UiState.Success -> {
                FullWidthColumnWithCenteredChildren {
                    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.success))
                    LottieAnimation(
                        composition = composition,
                    )
                    Text(
                        text = "Quiz created successfully!",
                        style = MaterialTheme.typography.h5,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    SelectionContainer {
                        Text(
                            text = "This is the ID for your quiz: ${state.data}",
                            style = MaterialTheme.typography.h6,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(Modifier.height(64.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        onClick = {
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "https://sanskar10100.tech/panel/quiz?code=${state.data}")
                                type = "text/plain"
                            }
                            startActivity(Intent.createChooser(intent, "Share quiz link"))
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null)
                        Text(text = "Share")
                    }
                }
            }
        }
    }
}