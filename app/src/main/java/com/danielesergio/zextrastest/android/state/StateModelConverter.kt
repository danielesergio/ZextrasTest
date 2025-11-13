package com.danielesergio.zextrastest.android.state

import com.danielesergio.zextrastest.R
import com.danielesergio.zextrastest.model.ValidationResult
import com.danielesergio.zextrastest.model.post.Post
import com.danielesergio.zextrastest.model.post.PostImp

//Model to UI state
fun Post.toPostState(): PostState = PostState(id!!, title, body)
fun List<Post>.toPostsState(): List<PostState> = map { it.toPostState() }

//UI state to Model
fun PostFormState.toPost(userId:Long): Post = PostImp(title = title, body = body, userId = userId)


//Error code to Android string id
fun ValidationResult.toStringID():Int?{
    return when(this){
        ValidationResult.VALID-> null
        ValidationResult.INVALID_EMPTY_FIELD -> R.string.error_empty_field_message
    }
}