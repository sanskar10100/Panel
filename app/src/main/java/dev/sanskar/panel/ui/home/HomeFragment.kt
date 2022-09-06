package dev.sanskar.panel.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import dev.sanskar.panel.util.clickWithRipple

class HomeFragment : Fragment() {

    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        if (Firebase.auth.currentUser == null) findNavController().navigate(R.id.action_homeFragment_to_loginFragment)

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
                        label = "Enter code",
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
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clickWithRipple {
                        findNavController().navigate(R.id.action_homeFragment_to_createFragment)
                    },
            ) {
                Text(
                    text = "Create Quiz",
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h3
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clickWithRipple {
                        showCodeInputDialog = true
                    },
            ) {
                Text(
                    text = "Play Quiz",
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h3
                )
            }
        }
    }
}