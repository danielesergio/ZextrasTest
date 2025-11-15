package com.danielesergio.zextrastest.model.datasource

import com.danielesergio.zextrastest.log.LoggerImpl
import com.danielesergio.zextrastest.model.post.Post
import kotlin.math.min

/**
 * A [LayeredDataSource] is a [DataSource] which simulate a mutable datasource starting from an immutable one.
 *
 * @property immutableDataSource the immutable data source that can't store new post.
 *
 * @property patchedDataSource the datasource that contained the new posts.
 */
class LayeredDataSource(val immutableDataSource: DataSource, val patchedDataSource: DataSource): DataSource{

    override suspend fun getPosts(page: Int?, after: Long?): List<Post> {
        val posts = immutableDataSource.getPosts(page)
        LoggerImpl.d(TAG, "Obtained post from immutableDatasource older than " +
                "($after), page $page, element in page ${posts.size}")
        val mergedPosts = when{
            page == null -> posts.plus(patchedDataSource.getPosts(page, after))
            posts.size < PAGE_SIZE -> {
                val patchedDataSourcePage =
                    (page * PAGE_SIZE - TOTAL_ELEMENTS_MAIN_DATASOURCE) / PAGE_SIZE
                val othersPosts = patchedDataSource.getPosts(patchedDataSourcePage, after)
                LoggerImpl.d(TAG, "Obtained post from patchedDataSource older " +
                        "than ($after), page $patchedDataSourcePage, element in page " +
                        "${othersPosts.size}")
                posts.plus(othersPosts).run {
                    this.subList(0, min(PAGE_SIZE, size))
                }
            }
            else -> posts
        }
        LoggerImpl.i(TAG, "Obtained post from LayeredDataSource,  older than " +
                "($after) page $page, element in page ${mergedPosts.size}")
        return mergedPosts
    }

    override suspend fun createPost(newPost: Post): Post {
        return immutableDataSource.createPost(newPost).run {
            LoggerImpl.d(TAG, "main data source successfully create the post " +
                    "$newPost")
            patchedDataSource.createPost(this).also {
                LoggerImpl.d(TAG, "second data source successfully create the post " +
                        "$newPost")
            }
        }.also {
            LoggerImpl.d(TAG, "Created Post")
        }
    }

    //todo move PAGE_SIZE and TOTAL_ELEMENTS_MAIN_DATASOURCE -> update dataSource interface to return these data.
    companion object{
        private const val PAGE_SIZE:Int = 10
        private const val TOTAL_ELEMENTS_MAIN_DATASOURCE:Int = 100

        private val TAG = LayeredDataSource::class.java.simpleName
    }
}