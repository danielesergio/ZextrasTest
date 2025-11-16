package com.danielesergio.zextrastest.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.danielesergio.zextrastest.R
import com.danielesergio.zextrastest.android.state.PostFormState
import com.danielesergio.zextrastest.android.state.toPost
import com.danielesergio.zextrastest.android.state.toStringID
import com.danielesergio.zextrastest.android.ui.RepositoryCreationExtrasKey
import com.danielesergio.zextrastest.log.LoggerImpl
import com.danielesergio.zextrastest.model.post.PostRepository
import com.danielesergio.zextrastest.model.post.PostValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostFormViewModel(
    private val postRepository: PostRepository
) : ViewModel() {

    private val initialState = PostFormState(title = "", titleError = PostValidator.validateTitle("").toStringID())
    private val _uiState = MutableStateFlow(initialState)
    // Public immutable flow
    private val _postCreateEvent = MutableStateFlow(false)

    val postCreateEvent = _postCreateEvent.asStateFlow()

    val uiState: StateFlow<PostFormState> = _uiState.asStateFlow()

    fun onTitleChanged(newTitle: String) {
        LoggerImpl.d(TAG, "Title changed to $newTitle")
        _uiState.update { cs ->
            cs.copy(
                title = newTitle,
                titleError = PostValidator.validateTitle(newTitle).toStringID()
            )
        }
    }

    fun onBodyChanged(newBody: String) {
        LoggerImpl.d(TAG, "Body changed to $newBody")
        _uiState.update { cs ->
            cs.copy(body = newBody)
        }
    }


    fun submit() {
        LoggerImpl.d(TAG, "Submit new post")
        _uiState.update { cs ->
            cs.copy(isPending = true)
        }

        viewModelScope.launch {
            postRepository.add(_uiState.value.toPost(1L))
                .onFailure {
                    LoggerImpl.i(TAG, "Submit new post fails")
                    _uiState.value = _uiState.value.copy(isPending = false, storingError = R.string.storing_error )
                }
                .onSuccess {
                    _uiState.value = initialState
                    _postCreateEvent.value = true
                    LoggerImpl.i(TAG, "Post successfully submitted")
                    _postCreateEvent.value = false
                }

        }
    }

    companion object{
        private val TAG = PostFormViewModel::class.java.simpleName

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val postRepository = this[RepositoryCreationExtrasKey] as PostRepository
                PostFormViewModel(postRepository)
            }
        }
    }
}