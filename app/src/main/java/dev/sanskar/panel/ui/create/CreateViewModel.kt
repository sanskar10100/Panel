package dev.sanskar.panel.ui.create

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dev.sanskar.panel.ui.components.AnswerBuilder
import dev.sanskar.panel.ui.data.AnswerType
import dev.sanskar.panel.ui.data.Question
import dev.sanskar.panel.util.STRING_SEPARATOR
import timber.log.Timber

class CreateViewModel : ViewModel() {
    var showSelector = derivedStateOf {
        questionText.isNotEmpty()
    }
    var answerType by mutableStateOf(AnswerType.NONE)

    val questions = mutableStateListOf<Question>()

    var addQuestionSnackbar by mutableStateOf("")

    var questionText by mutableStateOf("")

    fun selectAnswerType(type: AnswerType) {
        answerType = type
    }

    private fun clearState() {
        selectAnswerType(AnswerType.NONE)
        questionText = ""
    }

    fun addBinaryQuestion(correct: Boolean) {
        questions.add(Question(questionText, AnswerType.BINARY, correct.toString()))
        clearState()
        Timber.d("Added binary question, now list: $questions")
        addQuestionSnackbar = "Added binary question"

    }

    fun addMultipleChoiceQuestion(mcqBuilder: AnswerBuilder) {
        questions.add(Question(questionText, AnswerType.MCQ, mcqBuilder.selected[0], mcqBuilder.options))
        clearState()
        Timber.d("Added mcq question, now list: $questions")
        addQuestionSnackbar = "Added MCQ question"
    }

    fun addMultipleSelectQuestion(msqBuilder: AnswerBuilder) {
        questions.add(Question(questionText, AnswerType.MSQ, msqBuilder.selected.joinToString(
            STRING_SEPARATOR), msqBuilder.options))
        clearState()
        Timber.d("Added msq question, now list: $questions")
        addQuestionSnackbar = "Added MSQ question"
    }

    fun addTextQuestion(text: String) {
        questions.add(Question(text, AnswerType.TEXT, text))
        clearState()
        Timber.d("Added text question, now list: $questions")
        addQuestionSnackbar = "Added text question"
    }
}