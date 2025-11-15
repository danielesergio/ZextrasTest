package com.danielesergio.zextrastest.android

import com.danielesergio.zextrastest.client.PostService
import com.danielesergio.zextrastest.client.RemoteDataSource
import com.danielesergio.zextrastest.log.LoggerImpl
import com.danielesergio.zextrastest.model.datasource.LayeredDataSource
import com.danielesergio.zextrastest.model.datasource.DataSource
import com.danielesergio.zextrastest.model.datasource.FileDataSource
import com.danielesergio.zextrastest.model.post.Post
import com.danielesergio.zextrastest.model.post.PostImp
import com.danielesergio.zextrastest.model.post.PostImp.Companion.toPostImpl
import com.danielesergio.zextrastest.model.post.PostRepository
import com.danielesergio.zextrastest.model.post.PostSerializer
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
            immutableDataSource = RemoteDataSource(PostService.getInstance(dir!!)),
            patchedDataSource = FileDataSource(dir!!, postSerializer)
        )
    }

    val postRepository: PostRepository by lazy {
        LoggerImpl.d(TAG, "create repository")
        PostRepository(dataSource) }

    private val TAG = Factory::class.java.simpleName

}