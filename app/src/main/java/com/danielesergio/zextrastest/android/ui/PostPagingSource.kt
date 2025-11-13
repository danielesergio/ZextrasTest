package com.danielesergio.zextrastest.android.ui
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.danielesergio.zextrastest.android.state.PostState
import com.danielesergio.zextrastest.android.state.toPostsState
import com.danielesergio.zextrastest.model.post.PostRepository

private const val STARTING_KEY = 1

class PostPagingSource(private val postRepository: PostRepository) : PagingSource<Int, PostState>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostState> {
        val startKey = params.key ?: STARTING_KEY
        return postRepository.get(startKey)
            .map { posts -> LoadResult.Page(
                    data = posts.toPostsState(),
                    prevKey = when (startKey) {
                        STARTING_KEY -> null
                        else -> startKey -1 },
                    nextKey = startKey + 1
                )
            }.getOrElse {
                t -> LoadResult.Error(t)
            }


    }

    //todo study and possibly improve implementation
    override fun getRefreshKey(state: PagingState<Int, PostState>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

}