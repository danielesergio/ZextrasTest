package com.danielesergio.zextrastest.model.datasource

import com.danielesergio.zextrastest.model.post.Post

interface DataSource {
    suspend fun getPosts(page: Int?):List<Post>
    suspend fun createPost(newPost: Post): Post
}

