package com.danielesergio.zextrastest.model.post

import com.danielesergio.zextrastest.log.LoggerImpl
import com.danielesergio.zextrastest.model.ValidationResult
import com.danielesergio.zextrastest.model.datasource.DataSource

class PostRepository(private val dataSource: DataSource){

    suspend fun get(page:Int, pageSize:Int, before:Long): Result<List<Post>>{
        return runCatching {
            dataSource.getPosts(page, pageSize, before)
        }.onFailure {
            LoggerImpl.w(TAG, "Exception obtaining posts", it)
        }
    }

    suspend fun add(post: Post): Result<Post>{
        return runCatching {
            if(PostValidator.validateTitle(post.title) == ValidationResult.INVALID_EMPTY_FIELD){
                throw IllegalArgumentException("Title can't be empty")
            }
            dataSource.createPost(post)
        }.onFailure {
            LoggerImpl.w(TAG, "Exception adding $post", it)
        }
    }

    companion object{
        private val TAG = PostRepository::class.java.simpleName
    }

}