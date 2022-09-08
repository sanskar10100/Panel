package dev.sanskar.panel.ui.home

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel : ViewModel() {

    val validCode = MutableSharedFlow<String>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val error = MutableSharedFlow<String>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    fun checkCode(code: String) {
        val id = if (code.startsWith("https")) code.toUri().getQueryParameter("code") ?: "" else code
        Firebase
            .firestore
            .collection("quizzes")
            .document(id)
            .get()
            .addOnSuccessListener {
                Timber.d("Code status: ${it.exists()}")
                if (it.exists()) {
                    validCode.tryEmit(code)
                } else {
                    error.tryEmit("Invalid code")
                }
            }
            .addOnFailureListener {
                viewModelScope.launch { error.tryEmit("We couldn't verify this code. Please try again!") }
                Timber.d("Error in verifying code: $it")
            }
    }
}