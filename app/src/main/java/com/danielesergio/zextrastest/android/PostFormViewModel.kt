package com.danielesergio.zextrastest.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielesergio.zextrastest.R
import com.danielesergio.zextrastest.android.state.PostFormState
import com.danielesergio.zextrastest.android.state.toPost
import com.danielesergio.zextrastest.android.state.toStringID
import com.danielesergio.zextrastest.model.post.PostRepository
import com.danielesergio.zextrastest.model.post.PostValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostFormViewModel(
    private val validator: PostValidator = PostValidator,
    private val postRepository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostFormState())
    // Public immutable flow
    val uiState: StateFlow<PostFormState> = _uiState.asStateFlow()

    fun onTitleChanged(newTitle: String) {
        _uiState.update { cs ->
            cs.copy(
                title = newTitle,
                titleError = validator.validateTitle(newTitle).toStringID()
            )
        }
    }

    fun onBodyChanged(newBody: String) {
        _uiState.update { cs ->
            cs.copy(body = newBody)
        }
    }


    fun submit() {
        _uiState.update { cs ->
            cs.copy(isPending = true)
        }

        viewModelScope.launch {
            postRepository.add(_uiState.value.toPost(1L))
                .onFailure {
                    _uiState.value = _uiState.value.copy(isPending = false, storingError = R.string.storing_error )
                }
                .onSuccess {
                    _uiState.value = PostFormState()
                }
        }
    }
}