package com.danielesergio.zextrastest.android

import com.danielesergio.zextrastest.client.PostService
import com.danielesergio.zextrastest.client.RemoteDataSource
import com.danielesergio.zextrastest.log.LoggerImpl
import com.danielesergio.zextrastest.model.datasource.LayeredDataSource
import com.danielesergio.zextrastest.model.datasource.DataSource
import com.danielesergio.zextrastest.model.datasource.FileDataSource
import com.danielesergio.zextrastest.model.post.PostRepository
import java.io.File
import kotlin.properties.Delegates

object Factory {

    var dir: File? by Delegates.observable(null){ property, oldValue, newValue ->
        LoggerImpl.d(TAG, "dir set to ${newValue?.absolutePath}")
    }

    val dataSource: DataSource by lazy {
        LoggerImpl.d(TAG, "create data source")
        LayeredDataSource(
            immutableDataSource = RemoteDataSource(PostService.getInstance(dir!!)),
            patchedDataSource = FileDataSource.getInstance(dir!!)
        )
    }

    val postRepository: PostRepository by lazy {
        LoggerImpl.d(TAG, "create repository")
        PostRepository(dataSource) }

    private val TAG = Factory::class.java.simpleName

}