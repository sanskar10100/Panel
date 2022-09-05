package dev.sanskar.panel.ui.data

import dev.sanskar.panel.util.STRING_SEPARATOR

data class Question(
    val questionText: String,
    val type: AnswerType, // enum class ordinal
    val correct: String, // multiple answers separated by =),
    val options: List<String> = mutableListOf()
)

data class FirebaseQuestion(
    val questionText: String = "",
    val type: Int,
    val correct: String = "",
)

fun Question.toFirebaseQuestion() = FirebaseQuestion(
    questionText = questionText,
    type = type.ordinal,
    correct = correct
)

fun FirebaseQuestion.toQuestion(options: List<String>) = Question(
    questionText = questionText,
    type = AnswerType.values()[type],
    correct = correct,
    options = options
)

data class FirebaseOption(
    var optionText: String = ""
)

fun String.getMultipleCorrectAnswers() = this.split(STRING_SEPARATOR)