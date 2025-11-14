package com.danielesergio.zextrastest.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.danielesergio.zextrastest.android.state.PostState
import com.danielesergio.zextrastest.android.ui.PostPagingSource
import kotlinx.coroutines.flow.Flow

class PostsViewModel(
    sourceFactory:() -> PostPagingSource
): ViewModel() {

    val items: Flow<PagingData<PostState>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = sourceFactory
    ).flow.cachedIn(viewModelScope)

    companion object {

        // Define a custom key for your dependency
        val MY_REPOSITORY_KEY = object : CreationExtras.Key<PostPagingSource> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // Get the dependency in your factory
                val postPagingSource = this[MY_REPOSITORY_KEY] as PostPagingSource
                PostsViewModel {
                    postPagingSource
                }
            }
        }
    }
}
