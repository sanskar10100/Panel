package dev.sanskar.panel.ui.create

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class AnswerType {
    NONE,
    BINARY,
    MCQ,
    MSQ,
    TEXT
}

class CreateViewModel : ViewModel() {
    var showSelector by mutableStateOf(true)
    var answerType by mutableStateOf(AnswerType.NONE)

    var question = ""

    fun selectAnswerType(type: AnswerType) {
        answerType = type
    }
}