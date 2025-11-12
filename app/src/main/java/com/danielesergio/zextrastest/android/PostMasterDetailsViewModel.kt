package com.danielesergio.zextrastest.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielesergio.zextrastest.android.state.PostMasterDetailsState
import com.danielesergio.zextrastest.android.state.toPostsState
import com.danielesergio.zextrastest.model.post.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ViewModel
class PostMasterDetailsViewModel(
    private val repository: PostRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(PostMasterDetailsState())

    val uiState: StateFlow<PostMasterDetailsState> = _uiState.asStateFlow()

    fun selectPost(postIndex:Int) {
        val selected = _uiState.value.posts.getOrNull(postIndex)
        _uiState.value = _uiState.value.copy(selectedItem = selected)
        _uiState.update { cs ->
            cs.copy(selectedItem = _uiState.value.posts.getOrNull(postIndex))
        }
    }

    fun loadPost(){
        _uiState.update { cs ->
            cs.copy(isPending = true)
        }
        viewModelScope.launch {
            repository.get().onFailure {
                _uiState.update { cs -> cs.copy(syncError = true) }
            }.onSuccess { posts ->
                _uiState.update { posts.toPostsState() }
            }
        }
    }
}
