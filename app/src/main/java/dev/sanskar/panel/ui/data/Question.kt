package dev.sanskar.panel.ui.data

import dev.sanskar.panel.util.STRING_SEPARATOR

data class Question(
    val questionText: String = "",
    val type: AnswerType = AnswerType.NONE, // enum class ordinal
    val correct: String = "", // multiple answers separated by =),
    val options: List<String> = mutableListOf()
)

fun String.getMultipleCorrectAnswers() = this.split(STRING_SEPARATOR)