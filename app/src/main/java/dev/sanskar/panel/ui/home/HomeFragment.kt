package dev.sanskar.panel.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.sanskar.panel.R
import dev.sanskar.panel.ui.components.FullWidthColumnWithCenteredChildren
import dev.sanskar.panel.ui.components.PanelTextField
import dev.sanskar.panel.ui.theme.PanelTheme

class HomeFragment : Fragment() {

    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        if (Firebase.auth.currentUser == null) findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment(null))

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PanelTheme {
                    val scaffoldState = rememberScaffoldState()
                    Scaffold(
                        scaffoldState = scaffoldState
                    ) {
                        HomeScreen(Modifier.padding(it))
                    }

                    LaunchedEffect(Unit) {
                        viewModel.error.collect {
                            if (it.isNotEmpty()) scaffoldState.snackbarHostState.showSnackbar(it)
                        }
                    }

                    LaunchedEffect(Unit) {
                        viewModel.validCode.collect {
                            if (it.isNotEmpty()) findNavController()
                                .navigate(
                                    HomeFragmentDirections.actionHomeFragmentToPlayQuizFragment(it)
                                )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun HomeScreen(modifier: Modifier = Modifier) {
        var showCodeInputDialog by remember { mutableStateOf(false) }

        if (showCodeInputDialog) Dialog(
            onDismissRequest = { showCodeInputDialog = false },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Surface(
                elevation = 3.dp,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .padding(16.dp)
            ) {
                FullWidthColumnWithCenteredChildren(
                    modifier = Modifier.padding(16.dp)
                ) {
                    val (state, updateState) = remember { mutableStateOf("")}
                    PanelTextField(
                        state,
                        label = "Enter code or url",
                        placeholder = "eiehuheaeaweew",
                        icon = Icons.Default.Quiz,
                        onChanged = updateState,
                        onDone = {
                            showCodeInputDialog = false
                            viewModel.checkCode(state)
                        }
                    )
                }
            }
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFFFFFFF)),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LargeRoundButton(text = "Create Quiz", backgroundColor = Color(0xFFFFF176)) { findNavController().navigate(R.id.action_homeFragment_to_createFragment) }
            LargeRoundButton(text = "Play Quiz", backgroundColor = Color(0xFFFFB74D)) { showCodeInputDialog = true }
        }
    }

    @Composable
    fun ColumnScope.LargeRoundButton(
        text: String,
        modifier: Modifier = Modifier,
        backgroundColor: Color = Color(0xFFFF8A65),
        onClick: () -> Unit
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val pressed by interactionSource.collectIsPressedAsState()
        val elevation by animateDpAsState(if (pressed) 0.dp else 25.dp)
        Card(
            modifier = modifier
                .weight(1f)
                .fillMaxWidth(0.8f)
                .padding(vertical = 16.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = true,
                    onClick = onClick
                ),
            shape = CircleShape,
            backgroundColor = backgroundColor,
            elevation = elevation,
        ) {
            Box {
                Text(
                    text = text,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h3
                )
            }
        }
    }
}