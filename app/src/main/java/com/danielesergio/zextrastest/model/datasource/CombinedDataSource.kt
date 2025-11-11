package com.danielesergio.zextrastest.model.datasource

import com.danielesergio.zextrastest.model.post.Post
import kotlin.math.min

//todo find a better name
class CombinedDataSource(val mainDataSource: DataSource, val secondaryDataSource: DataSource): DataSource{
    override suspend fun getPosts(page: Int?): List<Post> {
        val posts = mainDataSource.getPosts(page)
        return when{
            page == null -> posts.plus(secondaryDataSource.getPosts(page))
            posts.size < PAGE_SIZE -> {
                val secondaryDataSourcePage =
                    (page * PAGE_SIZE - TOTAL_ELEMENTS_MAIN_DATASOURCE) / PAGE_SIZE
                posts.plus(secondaryDataSource.getPosts(secondaryDataSourcePage)).run {
                    this.subList(0, min(PAGE_SIZE, size))
                }
            }
            else -> posts
        }
    }

    override suspend fun createPost(newPost: Post): Post {
        return mainDataSource.createPost(newPost).run {
            secondaryDataSource.createPost(this)
        }
    }

    companion object{
        private const val PAGE_SIZE:Int = 10
        private const val TOTAL_ELEMENTS_MAIN_DATASOURCE:Int = 100
    }
}