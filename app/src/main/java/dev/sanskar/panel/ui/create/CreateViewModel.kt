package dev.sanskar.panel.ui.create

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.sanskar.panel.ui.components.AnswerBuilder
import dev.sanskar.panel.ui.data.AnswerType
import dev.sanskar.panel.ui.data.Question
import dev.sanskar.panel.ui.data.Quiz
import dev.sanskar.panel.util.STRING_SEPARATOR
import dev.sanskar.panel.util.UiState
import timber.log.Timber

class CreateViewModel : ViewModel() {
    var showSelector = derivedStateOf {
        questionText.isNotEmpty()
    }
    var answerType by mutableStateOf(AnswerType.NONE)

    val questions = mutableStateListOf<Question>()

    var addQuestionSnackbar by mutableStateOf("")

    var questionText by mutableStateOf("")

    var quizPushId by mutableStateOf<UiState<String>>(UiState.Loading)

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
        questions.add(Question(questionText, AnswerType.TEXT, text))
        clearState()
        Timber.d("Added text question, now list: $questions")
        addQuestionSnackbar = "Added text question"
    }

    fun pushQuizToFirebase() {
        quizPushId = UiState.Loading

        Firebase
            .firestore
            .collection("quizzes")
            .add(Quiz(questions.size))
            .addOnSuccessListener { ref ->
                questions.forEachIndexed { index, question ->
                    Firebase.firestore
                        .collection("quizzes")
                        .document(ref.id)
                        .collection("questions")
                        .document(index.toString())
                        .set(question)
                        .addOnSuccessListener {
                            if (index == questions.size - 1) {
                                quizPushId = UiState.Success(ref.id)
                            }
                        }
                        .addOnFailureListener {
                            quizPushId = UiState.Error("There was an error while creating your quiz, please try again!")
                            Timber.d("Error while pushing quiz to firebase: $it")
                            // Cleanup if unsuccessful
                            Firebase
                                .firestore
                                .collection("quizzes")
                                .document(ref.id)
                                .delete()
                        }
                }
            }
            .addOnFailureListener {
                quizPushId = UiState.Error("There was an error while creating your quiz, please try again!")
                Timber.d("Error while pushing quiz to firebase: $it")
            }
    }
}