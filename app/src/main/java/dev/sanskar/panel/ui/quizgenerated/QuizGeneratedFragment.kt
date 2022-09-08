package dev.sanskar.panel.ui.quizgenerated

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
import dev.sanskar.panel.ui.theme.PanelTheme
import dev.sanskar.panel.util.UiState
import dev.sanskar.panel.util.oneShotFlow

class QuizGeneratedFragment : Fragment() {
    private val viewModel by navGraphViewModels<CreateViewModel>(R.id.graph_create)
    val snackbarMessage = oneShotFlow<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.pushQuizToFirebase()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner))
            setContent {
                val state = rememberScaffoldState()
                Scaffold(
                    scaffoldState = state
                ) {
                    QuizGeneratedScreen(Modifier.padding(it))
                }

                LaunchedEffect(Unit) {
                    snackbarMessage.collect { message ->
                        state.snackbarHostState.showSnackbar(message)
                    }
                }
            }
        }
    }

    @Composable
    fun QuizGeneratedScreen(modifier: Modifier = Modifier) {
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
                SuccessContent(state, modifier)
            }
        }
    }

    @Composable
    private fun SuccessContent(state: UiState.Success<String>, modifier: Modifier = Modifier) {
        FullWidthColumnWithCenteredChildren(modifier) {
            val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.success))
            LottieAnimation(
                composition = composition,
            )
            Text(
                text = "Quiz created successfully!",
                style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))
            SelectionContainer {
                Text(
                    text = "This is the ID for your quiz: ${state.data}",
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(64.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        val intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT,
                                "https://sanskar10100.tech/panel/quiz?code=${state.data}")
                            type = "text/plain"
                        }
                        startActivity(Intent.createChooser(intent, "Share quiz link"))
                    }
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text(text = "Share")
                }
                val clipboardManager = LocalClipboardManager.current
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(state.data))
                        snackbarMessage.tryEmit("Copied to clipboard")
                    }
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text(text = "Copy")
                }
            }
        }
    }

    @Preview(showBackground = true, widthDp = 320)
    @Composable
    fun SuccessContentPreview() {
        PanelTheme {
            SuccessContent(state = UiState.Success("fjafuajafheuiyrbvsn"))
        }
    }
}