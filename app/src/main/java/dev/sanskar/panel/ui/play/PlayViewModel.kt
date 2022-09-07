package dev.sanskar.panel.ui.play

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.sanskar.panel.ui.data.AnswerState
import dev.sanskar.panel.ui.data.Question
import dev.sanskar.panel.ui.data.QuizStats
import dev.sanskar.panel.ui.data.getMultipleCorrectAnswers
import dev.sanskar.panel.util.UiState
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

class PlayViewModel : ViewModel() {
    var quizLoadState by mutableStateOf<UiState<Unit>>(UiState.Loading)
    var quizQuestions = mutableStateListOf<Question>()

    val nextPage = MutableSharedFlow<Boolean>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    fun loadQuiz(code: String) {
        quizLoadState = UiState.Loading
        Firebase
            .firestore
            .collection("quizzes")
            .document(code)
            .collection("questions")
            .get()
            .addOnSuccessListener {
                quizLoadState = if (it == null || it.isEmpty) {
                    UiState.Error("Invalid quiz code")
                } else {
                    val questions = it.toObjects(Question::class.java)
                    quizQuestions.addAll(questions)
                    UiState.Success(Unit)
                }
            }
            .addOnFailureListener {
                quizLoadState = UiState.Error(it.message ?: "Something went wrong")
            }
    }

    fun checkBinaryAnswer(question: Question, answer: Boolean) {
        setAnswerState(question) {
            answer.toString() == question.correct
        }
    }

    fun checkMultipleChoiceAnswer(question: Question, answer: String) {
        setAnswerState(question) {
            answer == question.correct
        }
    }

    fun checkMultipleSelectAnswer(question: Question, answers: List<String>) {
        setAnswerState(question) {
            answers.sorted() == question.correct.getMultipleCorrectAnswers().sorted()
        }
    }

    fun checkTextAnswer(question: Question, answer: String) {
        setAnswerState(question) {
            answer == question.correct
        }
    }

    private fun setAnswerState(question: Question, correct: () -> Boolean) {
        val index = quizQuestions.indexOf(question)
        if (correct()) {
            quizQuestions[index] = question.copy(answerState = AnswerState.Correct)
        } else {
            quizQuestions[index] = question.copy(answerState = AnswerState.Incorrect)
        }
        nextPage.tryEmit(true)
    }

    fun generateQuizStats() = QuizStats(
        correct = quizQuestions.count { it.answerState == AnswerState.Correct },
        incorrect = quizQuestions.count { it.answerState == AnswerState.Incorrect },
        skipped = quizQuestions.count { it.answerState == AnswerState.Unanswered },
        total = quizQuestions.size
    )
}