package dev.sanskar.panel.ui.play

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.sanskar.panel.ui.data.Question
import dev.sanskar.panel.util.UiState

class PlayViewModel : ViewModel() {
    var quizQuestions by mutableStateOf<UiState<List<Question>>>(UiState.Loading)

    fun loadQuiz(code: String) {
        quizQuestions = UiState.Loading
        Firebase
            .firestore
            .collection("quizzes")
            .document(code)
            .collection("questions")
            .get()
            .addOnSuccessListener {
                quizQuestions = if (it == null || it.isEmpty) {
                    UiState.Error("Invalid quiz code")
                } else {
                    val questions = it.toObjects(Question::class.java)
                    UiState.Success(questions)
                }
            }
            .addOnFailureListener {
                quizQuestions = UiState.Error(it.message ?: "Something went wrong")
            }

    }
}