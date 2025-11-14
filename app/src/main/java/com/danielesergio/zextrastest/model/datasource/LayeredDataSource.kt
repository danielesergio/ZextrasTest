package com.danielesergio.zextrastest.model.datasource

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

    override suspend fun getPosts(page: Int?): List<Post> {
        val posts = immutableDataSource.getPosts(page)
        return when{
            page == null -> posts.plus(patchedDataSource.getPosts(page))
            posts.size < PAGE_SIZE -> {
                val patchedDataSourcePage =
                    (page * PAGE_SIZE - TOTAL_ELEMENTS_MAIN_DATASOURCE) / PAGE_SIZE
                posts.plus(patchedDataSource.getPosts(patchedDataSourcePage)).run {
                    this.subList(0, min(PAGE_SIZE, size))
                }
            }
            else -> posts
        }
    }

    override suspend fun createPost(newPost: Post): Post {
        return immutableDataSource.createPost(newPost).run {
            patchedDataSource.createPost(this).also {
            }
        }
    }

    companion object{
        private const val PAGE_SIZE:Int = 10
        private const val TOTAL_ELEMENTS_MAIN_DATASOURCE:Int = 100

        private val TAG = LayeredDataSource::class.java.simpleName
    }
}