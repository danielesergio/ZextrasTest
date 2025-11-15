package com.danielesergio.zextrastest.model.post

interface PostSerializer {
    fun postToString(post:Post): String
    fun stringToPost(rawPost:String): Post
}