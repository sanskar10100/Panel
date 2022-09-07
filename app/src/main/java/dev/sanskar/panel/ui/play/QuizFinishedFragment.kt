package dev.sanskar.panel.ui.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import dev.sanskar.panel.ui.components.FullWidthColumnWithCenteredChildren
import dev.sanskar.panel.ui.data.QuizStats
import dev.sanskar.panel.ui.theme.PanelTheme

class QuizFinishedFragment : Fragment() {
    private val args by navArgs<QuizFinishedFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                QuizFinishedScreen(args.quizStats)
            }
        }
    }

    @Composable
    fun QuizFinishedScreen(quizStats: QuizStats,  modifier: Modifier = Modifier) {
        FullWidthColumnWithCenteredChildren(
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard(number = quizStats.correct, icon = Icons.Default.Check, Color(0xFF81C784))
            StatCard(number = quizStats.incorrect, icon = Icons.Default.Cancel, Color(0xFFF06292))
            StatCard(number = quizStats.skipped, icon = Icons.Default.Warning, Color(0xFFDCE775))
            StatCard(number = quizStats.total, icon = Icons.Default.Favorite, Color(0xFF64B5F6))
        }
    }

    @Composable
    fun StatCard(number: Int, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.8f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = 3.dp,
            color = color
        ) {
            FullWidthColumnWithCenteredChildren {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .scale(1.5f)
                        .padding(top = 16.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = number.toString(),
                    modifier = Modifier
                        .padding(16.dp),
                    style = MaterialTheme.typography.h4
                )
            }
        }
    }

    @Preview(showBackground = true, widthDp = 320)
    @Composable
    fun QuizFinishedScreenPreview() {
        PanelTheme {
            QuizFinishedScreen(quizStats = QuizStats(10, 5, skipped = 2, total = 17))
        }
    }
}