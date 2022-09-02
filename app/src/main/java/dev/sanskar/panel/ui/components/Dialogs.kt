package dev.sanskar.panel.ui.components

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ErrorDialog(
    message: String,
    modifier: Modifier = Modifier,
    title: String = "There was an error",
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(text = "Okay")
            }
        },
        title = { Text(text = title) },
        text = { Text(text = message) },
        modifier = modifier
    )
}