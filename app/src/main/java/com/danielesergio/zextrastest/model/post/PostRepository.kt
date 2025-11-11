package com.danielesergio.zextrastest.model.post

import com.danielesergio.zextrastest.model.datasource.DataSource

class PostRepository(private val dataSource: DataSource){

    suspend fun get(page:Int?): Result<List<Post>>{
        return runCatching { dataSource.getPosts(page) }
    }

    suspend fun add(post: Post): Result<Post>{
        return runCatching { dataSource.createPost(post) }
    }

}