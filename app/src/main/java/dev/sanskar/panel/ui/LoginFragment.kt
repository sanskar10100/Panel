package dev.sanskar.panel.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import dev.sanskar.panel.R
import dev.sanskar.panel.ui.theme.PanelTheme

class LoginFragment : Fragment() {

    private val args by navArgs<LoginFragmentArgs>()

    private val providers = arrayListOf(
        AuthUI.IdpConfig.GoogleBuilder().build()
    )

    private val signInIntent = AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .build()

    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) {
        if (it.resultCode == Activity.RESULT_OK) {
            if (args.code == null) findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            else findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToPlayQuizFragment(args.code!!))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PanelTheme {
                    LoginScreen()
                }
            }
        }
    }

    @Composable
    fun LoginScreen() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.hello))
            LottieAnimation(
                composition = composition,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .fillMaxHeight(0.7f)
            )

            Text(
                text = "Welcome to Panel!",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h4.copy(fontFamily = FontFamily(Font(R.font.calligraffitti))),
                color = Color(0xFF1A237E)
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { signInLauncher.launch(signInIntent) },
            ) {
                Text(
                    "Login with Google",
                    fontSize = 18.sp
                )
            }
        }
    }
}