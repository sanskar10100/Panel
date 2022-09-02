package dev.sanskar.panel.ui.data

data class Question(
    val questionText: String,
    val type: Int, // enum class ordinal
    val correct: String, // multiple answers separated by =),
    val option: List<String> = mutableListOf()
)