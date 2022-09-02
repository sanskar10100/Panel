package dev.sanskar.panel.ui.create

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dev.sanskar.panel.ui.components.MultipleAnswerBuilder
import dev.sanskar.panel.ui.data.AnswerType
import dev.sanskar.panel.ui.data.Question
import timber.log.Timber

class CreateViewModel : ViewModel() {
    var showSelector = derivedStateOf {
        questionText.isNotEmpty()
    }
    var answerType by mutableStateOf(AnswerType.NONE)

    val questions = mutableListOf<Question>()

    var snackbar by mutableStateOf("")

    var questionText by mutableStateOf("")

    fun selectAnswerType(type: AnswerType) {
        answerType = type
    }

    private fun clearState() {
        selectAnswerType(AnswerType.NONE)
        questionText = ""
    }

    fun addBinaryQuestion(correct: Boolean) {
        questions.add(Question(questionText, AnswerType.BINARY.ordinal, correct.toString()))
        clearState()
        Timber.d("Added binary question, now list: $questions")
        snackbar = "Added binary question"

    }

    fun addMultipleChoiceQuestion(mcqBuilder: MultipleAnswerBuilder) {
        questions.add(Question(questionText, AnswerType.MCQ.ordinal, mcqBuilder.selected[0], mcqBuilder.options))
        clearState()
        Timber.d("Added mcq question, now list: $questions")
        snackbar = "Added MCQ question"
    }

    fun addMultipleSelectQuestion(msqBuilder: MultipleAnswerBuilder) {
        questions.add(Question(questionText, AnswerType.MSQ.ordinal, msqBuilder.selected.joinToString(""), msqBuilder.options))
        clearState()
        Timber.d("Added msq question, now list: $questions")
        snackbar = "Added MSQ question"
    }

    fun addTextQuestion(text: String) {
        questions.add(Question(text, AnswerType.TEXT.ordinal, text))
        clearState()
        Timber.d("Added text question, now list: $questions")
        snackbar = "Added text question"
    }
}