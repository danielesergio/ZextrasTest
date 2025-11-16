package com.danielesergio.zextrastest.datasource

import com.danielesergio.zextrastest.domain.post.DataSource
import com.danielesergio.zextrastest.log.LoggerImpl
import com.danielesergio.zextrastest.domain.post.Post
import com.danielesergio.zextrastest.domain.post.PostSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

//Todo replace FileDataSourceWithADB
class FileDataSource(private val rootDir: File, private val postSerializer: PostSerializer):
    DataSource {

    override suspend fun getPosts(page: Int?, responseSize: Int?, before: Long?): List<Post> =
        withContext(Dispatchers.IO){
            val drop = if(page == null || responseSize == null){
                0
            } else {
                (page - 1) * responseSize
            }
            val posts = rootDir.listFiles{ file ->
                val creationTime = file.creationTime()

                file.name.startsWith(POST_FILE_PREFIX) &&
                        creationTime != null &&
                        (before == null || creationTime < before)
            }
                ?.sortedByDescending {
                it.name
            }?.drop(drop)?.take(responseSize?:Int.MAX_VALUE)?.mapNotNull { file ->
                runCatching {
                    LoggerImpl.d(TAG, "Deserializing post ${file.name}")
                    postSerializer.stringToPost(file.readText())
                }.onFailure {
                    LoggerImpl.w(TAG, "Can't deserialize post stored in ${file.absolutePath}", it)
                    LoggerImpl.w(TAG, "File content ${file.readText()}")
                }.getOrNull()
            }?: emptyList()

            LoggerImpl.i(TAG, "Obtained ${posts.size} posts, page = $page")
            posts
        }

    override suspend fun createPost(newPost: Post): Post = withContext(Dispatchers.IO){
        if(newPost.id == null){
            throw IllegalArgumentException("Id can't be null")
        }
        newPost.also {
            val destinationFile = File(rootDir, postFileName())
            destinationFile.writeText(postSerializer.postToString(postWithFixedID(newPost)))
            LoggerImpl.i(TAG, "Saved $it")
            LoggerImpl.d(TAG, "Stored in file ${destinationFile.absolutePath}")
        }
    }

    private  suspend fun postWithFixedID(p:Post):Post{
        val idOffset = getTotalPosts()
        return object:Post{
            override val id:Long? = p.id!! + idOffset
            override val userId: Long = p.userId
            override val body: String = p.body
            override val title: String = p.title

        }
    }

    //FIXME a file with a correct name but with an unserializable content is count.
    override suspend fun getTotalPosts(): Long = withContext(Dispatchers.IO){
        rootDir.listFiles { file ->
            file.name.startsWith(POST_FILE_PREFIX) &&
                    file.creationTime() != null
        }?.size?.toLong() ?: 0L
    }

    companion object{
        private const val POST_FILE_PREFIX = "post_"

        private fun File.creationTime():Long? = runCatching {
            this.name.removePrefix(POST_FILE_PREFIX).toLong()
        }.onFailure {
            LoggerImpl.w(TAG, "Can't get file creation time from ${this.name}")
        }.getOrNull()
        private fun postFileName() = "$POST_FILE_PREFIX${System.currentTimeMillis()}"

        private val TAG = FileDataSource::class.java.simpleName

    }
}