package com.danielesergio.zextrastest.android.ui
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.danielesergio.zextrastest.android.state.PostState
import com.danielesergio.zextrastest.android.state.toPostsState
import com.danielesergio.zextrastest.log.LoggerImpl
import com.danielesergio.zextrastest.domain.post.PostRepository

private const val STARTING_KEY = 1

class PostPagingSource(private val postRepository: PostRepository, private val pageSize:Int = 10) : PagingSource<Int, PostState>() {

    private val before = System.currentTimeMillis()

    //fixme handle post page different first time
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostState> {
        LoggerImpl.i(TAG, "Loading new post page ${params.key}, page size $pageSize")
        val startKey = params.key ?: STARTING_KEY
        return postRepository.get(startKey, pageSize, before)
            .map { posts -> LoadResult.Page(
                    data = posts.toPostsState(),
                    prevKey = when (startKey) {
                        STARTING_KEY -> null
                        else -> startKey -1 },
                    nextKey = if(posts.size < pageSize){
                        null
                    } else {
                        startKey+1
                    }
                )
            }.getOrElse {
                t ->
                LoggerImpl.w(TAG, "Loading new post page error", t)
                LoadResult.Error(t)
            }


    }

    //todo study and possibly improve implementation
    override fun getRefreshKey(state: PagingState<Int, PostState>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    companion object{
        private val TAG = PostPagingSource::class.java.simpleName
    }

}