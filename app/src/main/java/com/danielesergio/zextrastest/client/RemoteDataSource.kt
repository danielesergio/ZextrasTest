package com.danielesergio.zextrastest.client

import com.danielesergio.zextrastest.model.datasource.DataSource
import com.danielesergio.zextrastest.model.post.Post
import com.danielesergio.zextrastest.model.post.PostImp.Companion.toPostImpl

class RemoteDataSource(private val postService: PostService): DataSource {

    override suspend fun getPosts(
        page: Int?,
        responseSize: Int?,
        after: Long?
    ): List<Post>  = postService.getPosts(page, responseSize)

    override suspend fun createPost(newPost: Post): Post = postService.createPost(newPost.toPostImpl())

    override suspend fun getTotalPosts(): Long  = postService.getTotalPosts()

}