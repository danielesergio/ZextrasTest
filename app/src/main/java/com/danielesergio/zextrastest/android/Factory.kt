package com.danielesergio.zextrastest.android

import com.danielesergio.zextrastest.client.PostService
import com.danielesergio.zextrastest.datasource.RemoteDataSource
import com.danielesergio.zextrastest.log.LoggerImpl
import com.danielesergio.zextrastest.datasource.LayeredDataSource
import com.danielesergio.zextrastest.domain.post.DataSource
import com.danielesergio.zextrastest.datasource.FileDataSource
import com.danielesergio.zextrastest.domain.post.Post
import com.danielesergio.zextrastest.domain.post.PostImp
import com.danielesergio.zextrastest.domain.post.PostImp.Companion.toPostImpl
import com.danielesergio.zextrastest.domain.post.PostRepository
import com.danielesergio.zextrastest.domain.post.PostSerializer
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.properties.Delegates

object Factory {

    var dir: File? by Delegates.observable(null){ property, oldValue, newValue ->
        LoggerImpl.d(TAG, "dir set to ${newValue?.absolutePath}")
    }

    val postSerializer =object :PostSerializer{
        override fun postToString(post: Post): String {
            return Json.encodeToString(post.toPostImpl())
        }

        override fun stringToPost(rawPost: String): Post {
            return Json.decodeFromString<PostImp>(rawPost)
        }
    }
    val dataSource: DataSource by lazy {
        LoggerImpl.d(TAG, "create data source")
        LayeredDataSource(
            immutableDataSource = RemoteDataSource(PostService.getInstance()),
            patchedDataSource = FileDataSource(dir!!, postSerializer)
        )
    }

    val postRepository: PostRepository by lazy {
        LoggerImpl.d(TAG, "create repository")
        PostRepository(dataSource) }

    private val TAG = Factory::class.java.simpleName

}