package dev.sanskar.panel.util

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<String>()
}
