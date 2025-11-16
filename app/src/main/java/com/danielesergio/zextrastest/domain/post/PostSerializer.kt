package com.danielesergio.zextrastest.domain.post

interface PostSerializer {
    fun postToString(post:Post): String
    fun stringToPost(rawPost:String): Post
}