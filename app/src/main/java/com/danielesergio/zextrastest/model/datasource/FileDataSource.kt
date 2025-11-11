package com.danielesergio.zextrastest.model.datasource

import com.danielesergio.zextrastest.model.post.Post
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

interface PostSerialization{
    fun read(rawPost:String):Post
    fun write(post:Post):String
}


class FileDataSource private constructor(val rootDir: File):DataSource{

    override suspend fun getPosts(page: Int?): List<Post> {
        return rootDir.listFiles {
            it.name.startsWith("POST_FILE_PREFIX")
        }?.mapNotNull { file ->
            runCatching {
                    Json.decodeFromString<Post>(file.readText())
            }.getOrNull()
        }?: emptyList()
    }

    override suspend fun createPost(newPost: Post): Post {
        return newPost.also { File(rootDir, postFileName(it)).writeText(Json.encodeToString(it)) }
    }


    companion object{
        private val map:MutableMap<String,FileDataSource> = mutableMapOf()
        private const val POST_FILE_PREFIX = "post_"

        private fun postFileName(post:Post) = "$POST_FILE_PREFIX${post.id}"

        fun getInstance(file: File):FileDataSource{
            return map.getOrElse(file.absolutePath){
                FileDataSource(file).also { map[file.absolutePath] = it }
            }
        }
    }
}