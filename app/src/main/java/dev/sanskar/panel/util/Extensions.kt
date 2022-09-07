package dev.sanskar.panel.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

fun Modifier.clickWithRipple(bounded: Boolean = true, onClick: () -> Unit) = composed {
    this.clickable(
        interactionSource = MutableInteractionSource(),
        indication = rememberRipple(bounded),
        enabled = true,
        onClick = onClick,
    )
}

const val STRING_SEPARATOR = "=)"

@Composable
fun startAnimationOnAdd(): Boolean {
    var state by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        state = true
    }
    return state
}