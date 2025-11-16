package com.danielesergio.zextrastest.datasource

import com.danielesergio.zextrastest.client.PostService
import com.danielesergio.zextrastest.domain.post.DataSource
import com.danielesergio.zextrastest.domain.post.Post
import com.danielesergio.zextrastest.domain.post.PostImp.Companion.toPostImpl

class RemoteDataSource(private val postService: PostService): DataSource {

    override suspend fun getPosts(
        page: Int?,
        responseSize: Int?,
        before: Long?
    ): List<Post>  = postService.getPosts(page, responseSize)

    override suspend fun createPost(newPost: Post): Post = postService.createPost(newPost.toPostImpl())

    override suspend fun getTotalPosts(): Long  = postService.getTotalPosts()

}