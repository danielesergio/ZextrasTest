package com.danielesergio.zextrastest.model.datasource

import com.danielesergio.zextrastest.log.LoggerImpl
import com.danielesergio.zextrastest.model.post.Post
import kotlin.math.ceil
import kotlin.math.max

/**
 * A [LayeredDataSource] is a [DataSource] which simulate a mutable datasource starting from an immutable one.
 *
 * @property immutableDataSource the immutable data source that can't store new post.
 *
 * @property patchedDataSource the datasource that contained the new posts.
 */
//todo add test cases
class LayeredDataSource(val immutableDataSource: DataSource, val patchedDataSource: DataSource): DataSource{

    override suspend fun getPosts(page: Int?, responseSize: Int?, after: Long?): List<Post> {
        val posts = patchedDataSource.getPosts(page)
        LoggerImpl.d(TAG,logMessage("patchedDataSource", page, responseSize, after) )
        LoggerImpl.d(TAG, logTotalPost(posts.size))

        val mergedPosts = when{

            responseSize == null -> posts.plus(immutableDataSource.getPosts(page, responseSize,after))

            posts.size < responseSize -> {

                val othersPosts = getPostToMerge(page?:1,
                    responseSize,
                    after,
                    patchedDataSource.getTotalPosts())
                    .take(responseSize - posts.size)

                posts.plus(othersPosts)
            }

            else -> posts
        }
        LoggerImpl.i(TAG, logMessage("LayeredDataSource", page, responseSize, after))
        LoggerImpl.i(TAG, logTotalPost(mergedPosts.size) )

        return mergedPosts
    }

    override suspend fun createPost(newPost: Post): Post {
        return immutableDataSource.createPost(newPost).run {
            LoggerImpl.d(TAG, "main data source successfully create the post " +
                    "$newPost")
            patchedDataSource.createPost(this).also {
                LoggerImpl.d(TAG, "second data source successfully create the " +
                        "post $newPost")
            }
        }.also {
            LoggerImpl.i(TAG, "Created Post")
        }
    }

    override suspend fun getTotalPosts(): Long {
        return patchedDataSource.getTotalPosts() + immutableDataSource.getTotalPosts()
    }

    private suspend fun getPostToMerge(
        globalPage:Int,
        responseSize:Int,
        after: Long? ,
        totalElementOtherDS: Long ):List<Post>{
        val lastElementToShow = globalPage * responseSize - totalElementOtherDS
        if(lastElementToShow <= 0){
            LoggerImpl.d(TAG, "No post to merge")
            return emptyList()
        }
        val firstElementToShow = max(lastElementToShow - responseSize + 1, 1)
        val firstPage = ceil(firstElementToShow.toDouble() / responseSize).toInt()
        val secondPage = ceil(lastElementToShow.toDouble() / responseSize).toInt()
        LoggerImpl.d(TAG, "Post to merge from immutableDataSource [$firstElementToShow, $lastElementToShow]")
        LoggerImpl.d(TAG, "Page first element: $firstPage")
        LoggerImpl.d(TAG, "Page last element: $secondPage")
        val posts = immutableDataSource.getPosts(firstPage, responseSize, after)
            .drop(firstElementToShow.toInt() % responseSize - 1)
        return if(firstPage != secondPage) {
            posts + immutableDataSource.getPosts(secondPage, responseSize, after)
        } else {
            posts
        }
    }

    private fun logMessage(ds:String, page:Int?, responseSize:Int?, after:Long?) =
        "Obtained post from $ds.getPosts($page, $responseSize, $after)"
    private fun logTotalPost(totalPost:Int)= "Total posts obtained: $totalPost"
    companion object{
        private val TAG = LayeredDataSource::class.java.simpleName
    }
}