package com.danielesergio.zextrastest.android

import com.danielesergio.zextrastest.client.PostService
import com.danielesergio.zextrastest.model.datasource.LayeredDataSource
import com.danielesergio.zextrastest.model.datasource.DataSource
import com.danielesergio.zextrastest.model.datasource.DelegateDataSource
import com.danielesergio.zextrastest.model.datasource.FileDataSource
import com.danielesergio.zextrastest.model.post.PostRepository
import java.io.File

object Factory {

    var dir: File? = null

    val dataSource: DataSource by lazy {
        LayeredDataSource(
            immutableDataSource = DelegateDataSource(PostService.getInstance(dir!!)),
            patchedDataSource = FileDataSource.getInstance(dir!!)
        )
    }

    val postRepository: PostRepository by lazy { PostRepository(dataSource) }

}