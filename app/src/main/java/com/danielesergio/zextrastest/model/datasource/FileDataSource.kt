package com.danielesergio.zextrastest.model.datasource

import com.danielesergio.zextrastest.log.LoggerImpl
import com.danielesergio.zextrastest.model.post.Post
import kotlinx.serialization.json.Json
import java.io.File

class FileDataSource private constructor(val rootDir: File):DataSource{

    override suspend fun getPosts(page: Int?, responseSize: Int?, after: Long?): List<Post> {
        val posts = rootDir.listFiles{ file ->
            val creationTime = file.creationTime()

            file.name.startsWith("POST_FILE_PREFIX") &&
                    creationTime != null &&
                    (after == null || creationTime < after)
        }?.sortedByDescending {
            it.name
        }?.take(responseSize?:Int.MAX_VALUE)?.mapNotNull { file ->
            runCatching {
                    Json.decodeFromString<Post>(file.readText())
            }.getOrNull()
        }?: emptyList()

        LoggerImpl.i(TAG, "Obtained ${posts.size} posts, page = $page")
        return posts
    }

    override suspend fun createPost(newPost: Post): Post {
        return newPost.also {
            val destinationFile = File(rootDir, postFileName())
            destinationFile.writeText(Json.encodeToString(it))
            LoggerImpl.i(TAG, "Saved $it")
            LoggerImpl.d(TAG, "Stored in file ${destinationFile.absolutePath}")
        }
    }

    override suspend fun getTotalPosts(): Long {
        return rootDir.listFiles { file ->
            file.name.startsWith("POST_FILE_PREFIX") &&
                    file.creationTime() != null
        }?.size?.toLong() ?: 0L
    }

    companion object{
        private val map:MutableMap<String,FileDataSource> = mutableMapOf()
        private const val POST_FILE_PREFIX = "post_"

        private fun File.creationTime():Long? = runCatching {
            this.name.removePrefix(POST_FILE_PREFIX).toLong()
        }.onFailure {
            LoggerImpl.w(TAG, "Can't get file creation time from ${this.name}")
        }.getOrNull()
        private fun postFileName() = "$POST_FILE_PREFIX${System.currentTimeMillis()}"

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