package com.danielesergio.zextrastest.domain.post

interface DataSource {
    suspend fun getPosts(page: Int?,
                         responseSize: Int? = 10,
                         before:Long? =null):List<Post>
    suspend fun createPost(newPost: Post): Post

    suspend fun getTotalPosts():Long
}