package com.danielesergio.zextrastest.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.danielesergio.zextrastest.android.state.PostState
import com.danielesergio.zextrastest.android.ui.PostPagingSource
import com.danielesergio.zextrastest.android.ui.RepositoryCreationExtrasKey
import com.danielesergio.zextrastest.model.post.PostRepository
import kotlinx.coroutines.flow.Flow

class PostsViewModel(
    sourceFactory:() -> PostPagingSource
): ViewModel() {

    val items: Flow<PagingData<PostState>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = sourceFactory
    ).flow.cachedIn(viewModelScope)

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val postRepository = this[RepositoryCreationExtrasKey] as PostRepository
                PostsViewModel {
                    PostPagingSource(postRepository)
                }
            }
        }
    }
}
