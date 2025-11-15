package com.danielesergio.zextrastest.model.datasource

import com.danielesergio.zextrastest.log.LoggerImpl
import com.danielesergio.zextrastest.model.post.Post
import kotlinx.serialization.json.Json
import java.io.File

interface PostSerialization{
    fun read(rawPost:String):Post
    fun write(post:Post):String
}


class FileDataSource private constructor(val rootDir: File):DataSource{

    override suspend fun getPosts(page: Int?): List<Post> {
        val posts = rootDir.listFiles {
            it.name.startsWith("POST_FILE_PREFIX")
        }?.mapNotNull { file ->
            runCatching {
                    Json.decodeFromString<Post>(file.readText())
            }.getOrNull()
        }?: emptyList()

        LoggerImpl.i(TAG, "Obtained ${posts.size} posts, page = $page")
        return posts
    }

    override suspend fun createPost(newPost: Post): Post {
        return newPost.also {
            val destinationFile = File(rootDir, postFileName(it))
            destinationFile.writeText(Json.encodeToString(it))
            LoggerImpl.i(TAG, "Saved $it")
            LoggerImpl.d(TAG, "Stored in file $destinationFile")
        }
    }


    companion object{
        private val map:MutableMap<String,FileDataSource> = mutableMapOf()
        private const val POST_FILE_PREFIX = "post_"

        private fun postFileName(post:Post) = "$POST_FILE_PREFIX${post.id}"

        private val TAG = FileDataSource::class.java.simpleName
        fun getInstance(file: File):FileDataSource{
            return map.getOrElse(file.absolutePath){
                LoggerImpl.d(TAG, "Create new FileDataSource (${file.absolutePath})")
                FileDataSource(file).also { map[file.absolutePath] = it }
            }.also {
                LoggerImpl.i(TAG, "Get FileDataSource")
            }
        }
    }
}