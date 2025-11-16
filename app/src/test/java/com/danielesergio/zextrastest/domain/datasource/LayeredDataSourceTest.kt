package com.danielesergio.zextrastest.domain.datasource

import com.danielesergio.zextrastest.datasource.LayeredDataSource
import com.danielesergio.zextrastest.domain.post.DataSource
import com.danielesergio.zextrastest.domain.post.PostImp
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test



class LayeredDataSourceTest {

    private fun List<Int>.toPostList():List<PostImp> = map{ PostImp(it.toLong(), it.toLong(), "$it", "$it") }


    @Test
    fun testLayeredDataSourceCorrectlyMergePostFromDataSources():Unit = runBlocking{

        launch {
            val patchedPosts = (1..4).toList().toPostList()
            val immutablePosts = (5..10).toList().toPostList()

            val patchedDS = mockk<DataSource>()
            val immutableDS = mockk<DataSource>()
            coEvery  { patchedDS.getPosts(1, 5, any()) } returns patchedPosts
            coEvery { patchedDS.getTotalPosts() } returns patchedPosts.size.toLong()
            coEvery  { immutableDS.getPosts(1, 5, any()) } returns immutablePosts
            val layeredDataSource = LayeredDataSource(immutableDS, patchedDS)
            var result  = layeredDataSource.getPosts(1, 5)

            //first 4 element from patched data source fifth element is the first element of immutableDS
            assertEquals("Test merged patchedDataSource response with immutable data source ", patchedPosts + immutablePosts.first(), result)

            //test all 4 elements from patchedDS
            coEvery  { patchedDS.getPosts(1, 4, any()) } returns patchedPosts.dropLast(1)
            result  = layeredDataSource.getPosts(1, 4)
            assertEquals("Test only post from patched data source are returned", patchedPosts.dropLast(1), result)

            //test with patchedDS empty
            coEvery  { patchedDS.getPosts(1, 5, any()) } returns emptyList()
            result  = layeredDataSource.getPosts(1, 5)
            assertEquals("Test merged post with patchedDataSource empty", immutablePosts.take(5), result)

            //test with empty immutableDS
            coEvery  { patchedDS.getPosts(1, 10, any()) } returns patchedPosts
            coEvery  { immutableDS.getPosts(1, 10, any()) } returns emptyList()
            result  = layeredDataSource.getPosts(1, 10)
            assertEquals("Test merged post with immutableDS empty", patchedPosts, result)

            //test offset is correct for second page
            coEvery  { patchedDS.getPosts(3, 2, any()) } returns emptyList()
            coEvery  { patchedDS.getTotalPosts() } returns 2L
            coEvery  { immutableDS.getPosts(2, 2, any()) } returns immutablePosts.drop(2).take(2)

            result = layeredDataSource.getPosts(3,2)
            assertEquals("Test 3rd virtual page is correct, must be the second page of immutablePost", immutablePosts.drop(2).take(2), result)


        }

    }

    fun createPost() {
    }

    fun getTotalPosts() {
    }

}