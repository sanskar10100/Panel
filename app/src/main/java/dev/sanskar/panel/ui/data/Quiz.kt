package dev.sanskar.panel.ui.data

import java.io.Serializable

data class Quiz(
    val questionCount: Int = 0
)

data class QuizStats(
    val correct: Int = 0,
    val incorrect: Int = 0,
    val skipped: Int = 0,
    val total: Int = 0
) : Serializable