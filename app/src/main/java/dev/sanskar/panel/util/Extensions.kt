package dev.sanskar.panel.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
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